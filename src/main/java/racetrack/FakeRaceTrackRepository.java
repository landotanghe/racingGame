/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package racetrack;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FormattedTile;
import model.undergrounds.StandardUnderground;

/**
 *
 * @author lando
 */
public class FakeRaceTrackRepository implements IRaceTrackRepository{

    public FormattedTile[][] getRaceTrack(int raceId) {
        try {
            FormattedTile[][] tiles = new FormattedTile[3][3];
            tiles[1][0] = new FormattedTile("verticaal_start.png", "VS", new StandardUnderground());
            tiles[0][0] = new FormattedTile("L1.png", "VS", new StandardUnderground());
            tiles[0][1] = new FormattedTile("horizontaal.png", "H", new StandardUnderground());
            tiles[0][2] = new FormattedTile("L2.png", "VS", new StandardUnderground());
            tiles[1][2] = new FormattedTile("verticaal.png", "VS", new StandardUnderground());
            tiles[2][2] = new FormattedTile("L3.png", "VS", new StandardUnderground());
            tiles[2][1] = new FormattedTile("horizontaal.png", "H", new StandardUnderground());
            tiles[2][0] = new FormattedTile("L0.png", "H", new StandardUnderground());
            
            tiles[1][1] = new FormattedTile("empty.png", "E", new StandardUnderground());
            return tiles;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FakeRaceTrackRepository.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
    
}
