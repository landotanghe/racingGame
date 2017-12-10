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
import racetrack.FakeRaceTrackRepository;
import racetrack.IRaceTrackRepository;
import racetrack.RaceTrackRepository;

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
    private final ConnectionFactory _connectionFactory;
    private final IRaceTrackRepository _raceTrackRepository;

    private Point startLocation;

    private RaceInfo raceInfo;

    public Model() throws Exception {
        gids = new ArrayList<Integer>();
        _connectionFactory = new ConnectionFactory();
        //_raceTrackRepository = new RaceTrackRepository(_connectionFactory);
        _raceTrackRepository = new FakeRaceTrackRepository();
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
        } catch (SQLException e) {
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


    @Override
    public FormattedTile[][] getRaceTrack() {
        return _raceTrackRepository.getRaceTrack(raceInfo.getId());
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
