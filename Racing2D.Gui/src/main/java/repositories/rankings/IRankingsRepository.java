/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositories.rankings;

import java.util.ArrayList;
import model.Time;
import model.User;

/**
 *
 * @author lando
 */
public interface IRankingsRepository {

    ArrayList<Time> getRankings(int raceId, int topN);
    Time getPersonalBestTime(int raceId, User user); 
    void savePersonalBestTime(int raceId, int userId, int ms);
}
