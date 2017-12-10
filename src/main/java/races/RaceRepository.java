/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package races;

import data.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Model;
import model.RaceInfo;

/**
 *
 * @author lando
 */
public class RaceRepository implements IRaceRepository {
    private final ConnectionFactory _connectionFactory;

    public RaceRepository(ConnectionFactory _connectionFactory) {
        this._connectionFactory = _connectionFactory;
    }
        
    @Override
    public ArrayList<RaceInfo> getRaces(int count) {
        ArrayList<RaceInfo> races = new ArrayList<RaceInfo>();
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                fill(races, con, count);
            } finally {
                con.close();
            }
        } catch (SQLException e) {
            return new ArrayList<RaceInfo>();
        }

        return races;
    }
    
    private void fill(ArrayList<RaceInfo> races, Connection con, int count) {
        try {
            PreparedStatement stmt = con.prepareStatement("select top " + count + " * from tbl_circuits LIMIT 50"); //id, name, creator
            ResultSet rs = stmt.executeQuery();

            int i = 0;
            while (rs.next() && i < count) {
                PreparedStatement stmt2 = con.prepareStatement("select username from tbl_logins where id = ?");
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
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}
