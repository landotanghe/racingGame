/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.ghosts;

import java.util.ArrayList;
import model.GhostInfo;
import model.GhostTraject;
import model.User;

/**
 *
 * @author lando
 */
public interface IGhostRepository {

    void deleteGhost(int gid);

    ArrayList<GhostInfo> getGhostInfos(int raceId);

    ArrayList<GhostTraject> getGhostTrajects(ArrayList<Integer> gids);

    void saveGhost(int raceId, User user, GhostTraject ghost);
    
}
