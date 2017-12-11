/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.races;

import java.util.ArrayList;

/**
 *
 * @author lando
 */
public class FakeRaceRepository implements IRaceRepository{

    public RaceInfo[] getRaces(int count) {
        ArrayList<RaceInfo> races = new ArrayList<RaceInfo>();
        for (int i = 0; i < count; i++) {
            RaceInfo race = new RaceInfo(i, "automated" + i, "Lando");
            races.add(race);
        }
        return races.toArray(new RaceInfo[0]);
    }
    
}
