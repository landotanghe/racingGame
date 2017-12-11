/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.rankings;

import data.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import model.Model;
import model.Time;
import model.User;

/**
 *
 * @author lando
 */
public class RankingsRepository implements IRankingsRepository {
    private final ConnectionFactory _connectionFactory;

    public RankingsRepository(ConnectionFactory _connectionFactory) {
        this._connectionFactory = _connectionFactory;
    }
    
    @Override
    public ArrayList<Time> getRankings(int raceId, int topN) {
        ArrayList<Time> times = new ArrayList<Time>();
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement("select uid,time_in_ms from tbl_times where rid = ?");
                PreparedStatement stmt2 = con.prepareStatement("select username from tbl_logins where id = ?");
                stmt.setInt(1, raceId);
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
        } catch (SQLException e) {
            Logger.getLogger(Model.class.getName()).warning("Connection fails: using dummy for time table");
        }

        return times;
    }
    
    public void savePersonalBestTime(int raceId, int userId, int ms){
        Logger.getLogger(Model.class.getName()).info("saving time...");
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                con.setAutoCommit(false);
                PreparedStatement deleteOld = con.prepareStatement("delete from tbl_times where rid = ? and uid = ?");
                deleteOld.setInt(1, raceId);
                deleteOld.setInt(2, userId);
                deleteOld.execute();

                PreparedStatement insertNew = con.prepareStatement("insert into tbl_times (uid, rid, time_in_ms) values (?,?,?)");
                insertNew.setInt(1, userId);
                insertNew.setInt(2, raceId);
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
    
    public Time getPersonalBestTime(int raceId, User user) {
        Time time;
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement("select min(time_in_ms) from tbl_times where rid = ? and uid = ?");
                stmt.setInt(1, raceId);
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
}
