/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import data.ConnectionFactory;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import races.FakeRaceRepository;
import races.IRaceRepository;
import racetrack.FakeRaceTrackRepository;
import racetrack.IRaceTrackRepository;
import rankings.IRankingsRepository;
import rankings.RankingsRepository;

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
    private final IRaceRepository _raceRepository;
    private final IRankingsRepository _rankingsRepository;

    private Point startLocation;

    private RaceInfo raceInfo;

    public Model() throws Exception {
        gids = new ArrayList<Integer>();
        _connectionFactory = new ConnectionFactory();
        //_raceTrackRepository = new RaceTrackRepository(_connectionFactory);
        _raceTrackRepository = new FakeRaceTrackRepository();
        _raceRepository = new FakeRaceRepository();
        _rankingsRepository = new RankingsRepository(_connectionFactory);
    }

    @Override
    public ArrayList<RaceInfo> getRaceList(int count) {
        return _raceRepository.getRaces(count);
    }

    @Override
    public ArrayList<Time> getRankings(int topN) {
        return _rankingsRepository.getRankings(raceInfo.getId(), topN);
    }

    @Override
    public Time getPersonelBestTime() {
        return _rankingsRepository.getPersonelBestTime(raceInfo.getId(), user);
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
