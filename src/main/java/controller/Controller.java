/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import exceptions.LoginException;
import group06.desktop_racing_game.RaceDetailsPanel;
import model.Model;
import model.RaceInfo;
import group06.desktop_racing_game.App;
import group06.desktop_racing_game.GhostSelectPanel;
import group06.desktop_racing_game.IResizeOberver;
import login.LoginPanel;
import group06.desktop_racing_game.RaceOverviewPanel;
import java.util.ArrayList;
import login.Authenticator;
import login.BasicAuthenticator;
import login.FakeAuthenticator;
import login.LoginResult;
import model.GhostTraject;
import racing.RacingPanel;

/**
 *
 * @author Lando
 */
public class Controller implements IController {
    
    private final int RACES_PER_PAGE = 10;
    private final Model model;
    private final App app;

    @Override
    public int getScreenWidth() {
        return app.getWidth();
    }

    @Override
    public int getScreenHeight() {
        return app.getHeight();
    }

    public Controller(App app, Model model) {
        this.app = app;
        this.model = model;
    }

    @Override
    public void saveAll(GhostTraject ghost, int ms) {
        model.saveGhost(ghost);
        model.saveTime(ms);
    }

    @Override
    public void showLogin() {
        LoginPanel panel = new LoginPanel(this, model, app);
        app.setSize(298,160);
        app.setPanel(panel);
        app.setLocationRelativeTo(null);
    }

    @Override
    public void saveTime(int ms) {
        model.saveTime(ms);
    }

    @Override
    public void showRaceOverview() {    // toont een lijst van verschillende races waaruit de gebruiker kan kiezen
        RaceOverviewPanel panel = new RaceOverviewPanel(this, model);
        panel.fill();
        app.setPanel(panel);
        app.reconfigure();
    }

    @Override
    public void showRaceDetails(RaceInfo race) { //toont extra info over de geselecteerde race   
        model.setRaceInfo(race);
        RaceDetailsPanel panel = new RaceDetailsPanel(this, model, race);
        app.setPanel(panel);
        app.reconfigure();
    }

    @Override
    public void start(RaceInfo race) {
        model.setRaceInfo(race);
        RacingPanel panel = new RacingPanel(this, model);
        app.setPanel(panel);
        app.maximize();
    }

    @Override
    public void login(String login, String password) throws LoginException {
        Authenticator authenticator = new FakeAuthenticator();;
        LoginResult result = authenticator.login(login, password);
        if(result.isLoggedIn()){
            showRaceOverview();
        }
        else{
            throw new LoginException(result.getErrorMessage());
        }
        
    }

    @Override
    public void showGhostsPanel(RaceInfo race) {
        GhostSelectPanel panel = new GhostSelectPanel(this, model);
        panel.fill();
        app.setPanel(panel);
        app.reconfigure();
    }

    @Override
    public void selectGhosts(ArrayList<Integer> gids) {
        model.selectGhostIds(gids);
        app.setPanel(new RaceDetailsPanel(this, model, model.getRaceInfo()));
        app.reconfigure();
    }

    @Override
    public void addResizeObserver(IResizeOberver observer) {
        app.addResizeObserver(observer);
    }

}
