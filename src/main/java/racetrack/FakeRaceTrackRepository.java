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
            FormattedTile[][] tiles = new FormattedTile[1][1];
            tiles[0][0] = new FormattedTile("verticaal_start.png", "VS", new StandardUnderground());
            return tiles;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FakeRaceTrackRepository.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
    
}
