/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.ghosts;

import java.util.ArrayList;
import model.GhostInfo;
import model.GhostReplay;
import model.GhostTracker;
import model.User;

/**
 *
 * @author lando
 */
public interface IGhostRepository {

    void deleteGhost(int gid);

    ArrayList<GhostInfo> getGhostInfos(int raceId);

    ArrayList<GhostReplay> getGhostReplays(ArrayList<Integer> gids);

    void saveGhost(int raceId, User user, GhostTracker ghost);
    
}
