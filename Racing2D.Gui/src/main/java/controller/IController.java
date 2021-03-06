/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import exceptions.LoginException;
import views.IResizeOberver;
import java.util.ArrayList;
import model.GhostTracker;
import repositories.races.RaceInfo;

/**
 *
 * @author admin
 */
public interface IController {

    void addResizeObserver(IResizeOberver observer);

    int getScreenHeight();

    int getScreenWidth();

    void login(String login, String password) throws LoginException;

    void saveAll(GhostTracker ghost, int ms);

    void saveTime(int ms);

    void selectGhosts(ArrayList<Integer> gids);

    void showGhostsPanel(RaceInfo race);

    void showLogin();

    void showRaceDetails(RaceInfo race);

    void showRaceOverview();

    void start(RaceInfo race);
    
}
