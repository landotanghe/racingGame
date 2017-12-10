/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import data.ConnectionFactory;
import exceptions.LoginException;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.undergrounds.*;

/**
 *
 * contains data that is exchanged regularly between modules within the program
 * and communicates with the DB to store and load data
 *
 * @author Lando
 */
public class Model implements IModel {

    private ResourceBundle resources;
    private User user;
    private String password;
    private ArrayList<Integer> gids;
    private ConnectionFactory _connectionFactory;

    private Point startLocation;

    //UNDERGROUNDS
    private StandardUnderground standardUnderground;
    private RaceInfo raceInfo;

    public Model() throws Exception {
        //user = new User(852, "team06", "team06");
        gids = new ArrayList<Integer>();
        _connectionFactory = new ConnectionFactory();
        
        constructUndergrounds();
    }

    @Override
    public ArrayList<RaceInfo> getRaceList(int aantal) {
        ArrayList<RaceInfo> races = new ArrayList<RaceInfo>();
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                fill(races, con, aantal);
            } finally {
                con.close();
            }
        } catch (Exception e) {
            makeDummyRaces(races);
            Logger.getLogger(Model.class.getName()).warning("Connection fails: using dummy for races table");
        }

        return races;
    }

    @Override
    public ArrayList<Time> getTimes(int topN) {
        ArrayList<Time> times = new ArrayList<Time>();
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(resources.getString("select_times"));
                PreparedStatement stmt2 = con.prepareStatement(resources.getString("select_user_by_id"));
                stmt.setInt(1, raceInfo.getId());
                //stmt.setInt(topN, 2);
                ResultSet rs = stmt.executeQuery();
                while (rs.next() && times.size() < topN) {
                    int uid = rs.getInt(1);
                    int ms = rs.getInt(2);
                    stmt2.setInt(1, uid);
                    ResultSet rs2 = stmt2.executeQuery();
                    rs2.next();
                    String username = rs2.getString(1);
                    Time time = new Time(username, uid, ms);
                    times.add(time);
                }

            } finally {
                con.close();
            }
        } catch (Exception e) {
            Logger.getLogger(Model.class.getName()).warning("Connection fails: using dummy for time table");
            // makeDummyTimes(times);
        }

        return times;
    }

    @Override
    public Time getPersonelBestTime() {
        Time time;
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(resources.getString("select_personel_best_time"));
                stmt.setInt(1, raceInfo.getId());
                stmt.setInt(2, user.getId());
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int ms = rs.getInt(1);
                time = new Time(user.getUsername(), user.getId(), ms);

            } finally {
                con.close();
            }
        } catch (Exception e) {
            time = new Time(user.getUsername(), user.getId(), -1);
            // makeDummyTimes(times);
        }

        return time;
    }

    private void makeDummyTimes(Time[] times) {
        for (int i = 0; i < times.length; i++) {
            Time time = new Time(password, 0, 100);
            times[i] = time;
        }
    }

    private void makeDummyRaces(ArrayList<RaceInfo> races) {
        for (int i = 0; i < 10; i++) {
            RaceInfo race = new RaceInfo(i, "automated" + i, "Lando");
            races.add(race);
        }
    }

    private void fill(ArrayList<RaceInfo> races, Connection con, int aantal) {
        try {
            PreparedStatement stmt = con.prepareStatement(resources.getString("select_races")); //id, name, creator
            //stmt.setInt(1, 50);
            ResultSet rs = stmt.executeQuery();

            int i = 0;
            while (rs.next() && i < aantal) {
                PreparedStatement stmt2 = con.prepareStatement(resources.getString("get_name"));
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int creator = rs.getInt(3);
                stmt2.setInt(1, creator);
                ResultSet rs2 = stmt2.executeQuery();
                rs2.next();
                String creatorname = rs2.getString(1);

                RaceInfo race = new RaceInfo(id, name, creatorname);
                races.add(race);
                i++;
                // stmt2.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    

    private void constructUndergrounds() {
        standardUnderground = new StandardUnderground();
    }

    private void fillTrack(Connection con, FormattedTile[][] raceTrack, int rid, Map<Integer, String> tilenames, Map<Integer, String> tilecodes) {
        try {
            PreparedStatement stmt = con.prepareStatement(resources.getString("select_race_tiles")); //rid,tid,x,y
            stmt.setInt(1, rid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                //int tid;
                int tid = rs.getInt(2);
                int x = rs.getInt(3);
                int y = rs.getInt(4);
                String tilename = tilenames.get(tid);
                String tilecode = tilecodes.get(tid);
                raceTrack[y][x] = new FormattedTile(tilename, tilecode, standardUnderground);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).warning("could not fill track from DB");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillTileMap(Map<Integer, String> filemap, Map<Integer, String> codemap, Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(resources.getString("select_tile_names"));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int tid = rs.getInt(1);
            String filename = rs.getString(2);
            String code = rs.getString(3);
            filemap.put(tid, filename);
            codemap.put(tid, code);
        }
    }

    @Override
    public FormattedTile[][] getRaceTrack() {
        //Connection con = getConnection();
        int rows = 10; //max van y
        int columns = 10; //max van x
        FormattedTile[][] raceTrack = new FormattedTile[rows][columns];

        try {
            Connection con = _connectionFactory.getConnection();
            try {

                Logger.getLogger(Model.class.getName()).info("request tilenames");
                Map<Integer, String> tilenames = new HashMap<Integer, String>();
                Map<Integer, String> tilecodes = new HashMap<Integer, String>();
                fillTileMap(tilenames, tilecodes, con);

                //ondergrond ophalen: hier of binnen lus afh of alle tegels zelfde ondergrond of niet...
                Logger.getLogger(Model.class.getName()).info("filling grid with empty tiles");
                makeEmptyGrid(raceTrack, rows, columns, tilenames, tilecodes);
                Logger.getLogger(Model.class.getName()).info("filling grid with real tiles");
                fillTrack(con, raceTrack, raceInfo.getId(), tilenames, tilecodes);

            } catch (FileNotFoundException ex) {
                //tijdelijk als con plat
                Logger.getLogger(Model.class.getName()).warning("making empty grid");
                Map<Integer, String> tilenames = new HashMap<Integer, String>();
                tilenames.put(11, "empty.png");
                try {
                    Map<Integer, String> tilecodes = new HashMap<Integer, String>();
                    makeEmptyGrid(raceTrack, rows, columns, tilenames, tilecodes);
                } catch (FileNotFoundException ex1) {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex1);
                }//eind tijdelijk als con plat
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                con.close();
            };
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        return raceTrack;
    }

    private IUnderground chooseUnderground(String code) {
        IUnderground underground = standardUnderground;
        if (code == "") {

        } else if (code == "") {

        }
        return underground;
    }

    @Override
    public Point getStartLocation() {
        return startLocation;
    }

    @Override
    public void setRaceInfo(RaceInfo raceInfo) {
        this.raceInfo = raceInfo;
    }

    @Override
    public void saveGhost(GhostTraject ghost) {
        Logger.getLogger(Model.class.getName()).info("saving ghostdata...");
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                con.setAutoCommit(false);
                //rij in tbl_ghosts
                PreparedStatement ghost_stmt = con.prepareStatement(resources.getString("insert_ghost"));
                ghost_stmt.setInt(1, raceInfo.getId());
                ghost_stmt.setInt(2, user.getId());
                ghost_stmt.execute();

                //huidige gid ophalen
                PreparedStatement gid_stmt = con.prepareStatement(resources.getString("select_max_ghostid"));
                gid_stmt.setInt(1, raceInfo.getId());
                gid_stmt.setInt(2, user.getId());
                ResultSet rs = gid_stmt.executeQuery();
                rs.next();
                int gid = rs.getInt(1);

                //rijen in tbl_ghost_ticks
                ghost.resetTick();
                PreparedStatement ghost_tick_stmt = con.prepareStatement(resources.getString("insert_ghost_tick"));
                ghost_tick_stmt.setInt(1, gid);
                int tick = 0;
                while (ghost.nextTick()) {
                    ghost_tick_stmt.setInt(2, tick);
                    ghost_tick_stmt.setInt(3, ghost.getX());
                    ghost_tick_stmt.setInt(4, ghost.getY());
                    ghost_tick_stmt.setDouble(5, ghost.getTheta());
                    ghost_tick_stmt.setDouble(6, ghost.getMillis());
                    ghost_tick_stmt.execute();
                    tick++;
                }
                con.commit();
                Logger.getLogger(Model.class.getName()).info("ghostdata succesfully saved");
            } finally {
                con.rollback();
                con.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).warning("could not update ghostdata");
        }
    }

    @Override
    public void deleteGhost(int gid) {
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                con.setAutoCommit(false);
                //tbl_ghosts
                PreparedStatement delete_ghost_stmt = con.prepareStatement(resources.getString("delete_ghost"));
                delete_ghost_stmt.setInt(1, gid);
                delete_ghost_stmt.execute();

                //tbl_ghost_ticks
                PreparedStatement delete_ghost_tick_stmt = con.prepareStatement(resources.getString("delete_ghost_ticks"));
                delete_ghost_stmt.setInt(1, gid);
                delete_ghost_stmt.execute();
                con.commit();
            } catch (Exception ex) {
                con.rollback();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).warning("could not delete ghostdata");

        }
    }

    @Override
    public ArrayList<GhostTraject> getGhostTrajects() {
        ArrayList<GhostTraject> ghosts = new ArrayList<GhostTraject>();
        for (int i = 0; i < gids.size(); i++) {
            GhostTraject ghost = getGhostTraject(gids.get(i));
            if (ghost != null) {
                ghosts.add(ghost);
            }
        }
        return ghosts;
    }

    private GhostTraject getGhostTraject(int gid) {
        GhostTraject ghost = null;
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                //aantal ticks opvragen
                PreparedStatement aantalStmt = con.prepareStatement(resources.getString("select_ghost_tick_size"));
                aantalStmt.setInt(1, gid);
                ResultSet r = aantalStmt.executeQuery();
                r.next();
                int size = r.getInt(1);
                if (size == 0) {
                    return null;
                } else {
                    ghost = new GhostTraject(r.getInt(1));
                }
                //ticks inladen in ghost
                PreparedStatement stmt = con.prepareStatement(resources.getString("select_ghost_ticks"));
                stmt.setInt(1, gid);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int tick_nr = rs.getInt(1);
                    int x = rs.getInt(2);
                    int y = rs.getInt(3);
                    double theta = rs.getDouble(4);
                    int ms = rs.getInt(5);

                    ghost.addTick(x, y, theta, ms, tick_nr);
                }
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).warning("could not load ghost");
            return null;
        }
        return ghost;
    }

    @Override
    public ArrayList<GhostInfo> getGhostInfos() {
        ArrayList<GhostInfo> ghosts = new ArrayList<GhostInfo>();
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(resources.getString("select_ghosts"));
                stmt.setInt(1, raceInfo.getId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int gid = rs.getInt(1);
                    int uid = rs.getInt(2);
                    PreparedStatement stmt2 = con.prepareStatement(resources.getString("select_user_by_id"));
                    stmt2.setInt(1, uid);
                    ResultSet rs2 = stmt2.executeQuery();
                    rs2.next();
                    String username = rs2.getString(1);

                    //tijd ophalen vd ghost adhv gid
                    PreparedStatement time_stmt = con.prepareStatement(resources.getString("select_ghost_time"));
                    time_stmt.setInt(1, gid);
                    ResultSet time_rs = time_stmt.executeQuery();
                    time_rs.next();
                    Time time = new Time(username, uid, time_rs.getInt(1));
                    ghosts.add(new GhostInfo(gid, uid, username, time));
                }
            } finally {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).warning("could not get ghostInfo");
        }
        return ghosts;
    }

    private void makeEmptyGrid(FormattedTile[][] raceTrack, int rows, int columns, Map<Integer, String> tilenames, Map<Integer, String> codenames) throws FileNotFoundException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Object fileName = tilenames.get(11);
                raceTrack[i][j] = new FormattedTile((String) fileName, "EM", standardUnderground);
                //Logger.getLogger(Model.class.getName()).info("loaded file " + fileName);
            }
        }
    }

    @Override
    public void saveTime(int ms) {
        Logger.getLogger(Model.class.getName()).info("saving time...");
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                con.setAutoCommit(false);
                PreparedStatement deleteOld = con.prepareStatement(resources.getString("delete_time"));
                deleteOld.setInt(1, raceInfo.getId());
                deleteOld.setInt(2, user.getId());
                deleteOld.execute();

                PreparedStatement insertNew = con.prepareStatement(resources.getString("insert_time"));
                insertNew.setInt(1, user.getId());
                insertNew.setInt(2, raceInfo.getId());
                insertNew.setInt(3, ms);
                insertNew.execute();
                con.commit();
                Logger.getLogger(Model.class.getName()).info("time succesfully saved");
            } catch (SQLException e) {
                con.rollback();
            } finally {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).warning("could not save time");
        }
    }

    @Override
    public void selectGhostIds(ArrayList<Integer> gids) {
        this.gids = gids;
    }

    @Override
    public RaceInfo getRaceInfo() {
        return raceInfo;
    }

}
