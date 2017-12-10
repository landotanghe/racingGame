/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package races;

import java.util.ArrayList;
import model.RaceInfo;

/**
 *
 * @author lando
 */
public class FakeRaceRepository implements IRaceRepository{

    public ArrayList<RaceInfo> getRaces(int count) {
        ArrayList<RaceInfo> races = new ArrayList<RaceInfo>();
        for (int i = 0; i < count; i++) {
            RaceInfo race = new RaceInfo(i, "automated" + i, "Lando");
            races.add(race);
        }
        return races;
    }
    
}
