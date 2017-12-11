/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.racetrack;

import data.ConnectionFactory;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FormattedTile;
import model.Model;
import model.undergrounds.StandardUnderground;

/**
 *
 * @author lando
 */
public class RaceTrackRepository implements IRaceTrackRepository {
    private final ConnectionFactory _connectionFactory;

    public RaceTrackRepository(ConnectionFactory _connectionFactory) {
        this._connectionFactory = _connectionFactory;
    }
        
    @Override
    public FormattedTile[][] getRaceTrack(int raceId) {
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
                fillTrack(con, raceTrack, raceId, tilenames, tilecodes);

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
    
    private void fillTrack(Connection con, FormattedTile[][] raceTrack, int rid, Map<Integer, String> tilenames, Map<Integer, String> tilecodes) {
        try {
            PreparedStatement stmt = con.prepareStatement("select * from tbl_circuit_tiles where rid = ?"); //rid,tid,x,y
            stmt.setInt(1, rid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                //int tid;
                int tid = rs.getInt(2);
                int x = rs.getInt(3);
                int y = rs.getInt(4);
                String tilename = tilenames.get(tid);
                String tilecode = tilecodes.get(tid);
                raceTrack[y][x] = new FormattedTile(tilename, tilecode, new StandardUnderground());
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).warning("could not fill track from DB");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fillTileMap(Map<Integer, String> filemap, Map<Integer, String> codemap, Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("select ID_Tile, file, code FROM tbl_tiles");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int tid = rs.getInt(1);
            String filename = rs.getString(2);
            String code = rs.getString(3);
            filemap.put(tid, filename);
            codemap.put(tid, code);
        }
    }
    
    
    private void makeEmptyGrid(FormattedTile[][] raceTrack, int rows, int columns, Map<Integer, String> tilenames, Map<Integer, String> codenames) throws FileNotFoundException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Object fileName = tilenames.get(11);
                raceTrack[i][j] = new FormattedTile((String) fileName, "EM", new StandardUnderground());
            }
        }
    }
}
