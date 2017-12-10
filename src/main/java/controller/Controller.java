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
import group06.desktop_racing_game.LoginPanel;
import group06.desktop_racing_game.RaceOverviewPanel;
import java.util.ArrayList;
import javax.swing.JPanel;
import model.GhostTraject;
import racing.RacingPanel;
import racing.Track;

/**
 *
 * @author Lando
 */
public class Controller implements IController {
    // zorgt voor communicatie tussen verschillende panelen en het model
    
    private final int RACES_PER_PAGE = 10;
    private Model model;
    private App app;

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
        final int MIN_LOGIN_LENGTH = 3,
                MAX_LOGIN_LENGTH = 16,
                MIN_PASS_LENGTH = 6,
                MAX_PASS_LENGTH = 16;
        //check login en password
        if (login == null || login.length() == 0) {
            throw new LoginException("login required");
        } else if (login.length() < MIN_LOGIN_LENGTH || login.length() > MAX_LOGIN_LENGTH) {
            throw new LoginException("login must be " + MIN_LOGIN_LENGTH + " - " + MAX_LOGIN_LENGTH + "chars");
        } else if (password == null || password.length() == 0) {
            throw new LoginException("password required");
        } else if (password.length() < MIN_PASS_LENGTH || password.length() > MAX_PASS_LENGTH) {
            throw new LoginException("password must be " + MIN_LOGIN_LENGTH + " - " + MAX_LOGIN_LENGTH + " chars");
        }
        model.login(login, password);
        showRaceOverview();
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
