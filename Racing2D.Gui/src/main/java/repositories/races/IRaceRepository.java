/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.races;

import java.util.ArrayList;
import model.RaceInfo;

/**
 *
 * @author lando
 */
public interface IRaceRepository {

    ArrayList<RaceInfo> getRaces(int aantal);
    
}
