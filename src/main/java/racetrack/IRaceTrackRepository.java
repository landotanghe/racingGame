/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package racetrack;

import model.FormattedTile;

/**
 *
 * @author lando
 */
public interface IRaceTrackRepository {

    FormattedTile[][] getRaceTrack(int raceId);
    
}
