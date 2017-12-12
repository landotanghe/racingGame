/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.ghosts;

import data.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import model.GhostInfo;
import model.GhostReplay;
import model.GhostTracker;
import model.Model;
import model.Time;
import model.User;
import model.positioning.Location;
import model.positioning.Orientation;
import model.positioning.Positioning;

/**
 *
 * @author lando
 */
public class GhostRepository implements IGhostRepository {
    private final ConnectionFactory _connectionFactory;

    public GhostRepository(ConnectionFactory _connectionFactory) {
        this._connectionFactory = _connectionFactory;
    }
    
    @Override
    public void saveGhost(int raceId, User user, GhostTracker ghost) {
        Logger.getLogger(Model.class.getName()).info("saving ghostdata...");
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                con.setAutoCommit(false);
                //rij in tbl_ghosts
                PreparedStatement ghost_stmt = con.prepareStatement("insert into tbl_ghosts (rid,uid) values (?,?)");
                ghost_stmt.setInt(1, raceId);
                ghost_stmt.setInt(2, user.getId());
                ghost_stmt.execute();

                //huidige gid ophalen
                PreparedStatement gid_stmt = con.prepareStatement("select max(id) from tbl_ghosts where rid = ? and uid = ?");
                gid_stmt.setInt(1, raceId);
                gid_stmt.setInt(2, user.getId());
                ResultSet rs = gid_stmt.executeQuery();
                rs.next();
                int gid = rs.getInt(1);

                //rijen in tbl_ghost_ticks
                ghost.resetTick();
                PreparedStatement ghost_tick_stmt = con.prepareStatement("INSERT INTO tbl_ghost_ticks (gid,tick_nr,x,y,theta,ms) VALUES (?,?,?,?,?,?)");
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
    public void deleteGhost(int ghostId) {
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                con.setAutoCommit(false);
                //tbl_ghosts
                PreparedStatement delete_ghost_stmt = con.prepareStatement("delete from tbl_ghosts where id = ?");
                delete_ghost_stmt.setInt(1, ghostId);
                delete_ghost_stmt.execute();

                //tbl_ghost_ticks
                PreparedStatement delete_ghost_tick_stmt = con.prepareStatement("delete from tbl_ghost_ticks where gid = ?");
                delete_ghost_stmt.setInt(1, ghostId);
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
    public ArrayList<GhostReplay> getGhostReplays(ArrayList<Integer> ghostIds) {
        ArrayList<GhostReplay> ghosts = new ArrayList<GhostReplay>();
        for (int i = 0; i < ghostIds.size(); i++) {
            GhostReplay ghost = getGhostTraject(ghostIds.get(i));
            if (ghost != null) {
                ghosts.add(ghost);
            }
        }
        return ghosts;
    }

    private GhostReplay getGhostTraject(int ghostId) {
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                ArrayList<Integer> millis = new ArrayList<Integer>();
                ArrayList<Positioning> positions = new ArrayList<Positioning>();
                
                //aantal ticks opvragen
                PreparedStatement aantalStmt = con.prepareStatement("select count(1) from tbl_ghost_ticks where gid = ?");
                aantalStmt.setInt(1, ghostId);
                ResultSet r = aantalStmt.executeQuery();
                r.next();
                int size = r.getInt(1);
                if (size == 0) {
                    return null;
                }
                //ticks inladen in ghost
                PreparedStatement stmt = con.prepareStatement("select tick_nr,x,y,theta,ms FROM tbl_ghost_ticks where gid=? order by tick_nr");
                stmt.setInt(1, ghostId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int tick_nr = rs.getInt(1);
                    int x = rs.getInt(2);
                    int y = rs.getInt(3);
                    double theta = rs.getDouble(4);
                    int ms = rs.getInt(5);
                    
                    Positioning p = new Positioning(new Location(x, y), new Orientation(theta));
                    millis.add(ms);
                    positions.add(p);
                }
                return new GhostReplay(millis, positions);
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).warning("could not load ghost");
            return null;
        }
    }

    @Override
    public ArrayList<GhostInfo> getGhostInfos(int raceId) {
        ArrayList<GhostInfo> ghosts = new ArrayList<GhostInfo>();
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement("select id, uid from tbl_ghosts where rid = ?");
                stmt.setInt(1, raceId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int ghostId = rs.getInt(1);
                    int uid = rs.getInt(2);
                    PreparedStatement stmt2 = con.prepareStatement("select username from tbl_logins where id = ?");
                    stmt2.setInt(1, uid);
                    ResultSet rs2 = stmt2.executeQuery();
                    rs2.next();
                    String username = rs2.getString(1);

                    //tijd ophalen vd ghost adhv gid
                    PreparedStatement time_stmt = con.prepareStatement("select max(ms) from tbl_ghost_ticks where gid =?");
                    time_stmt.setInt(1, ghostId);
                    ResultSet time_rs = time_stmt.executeQuery();
                    time_rs.next();
                    Time time = new Time(username, uid, time_rs.getInt(1));
                    ghosts.add(new GhostInfo(ghostId, uid, username, time));
                }
            } finally {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).warning("could not get ghostInfo");
        }
        return ghosts;
    }
}
