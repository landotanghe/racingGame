/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.ConnectionFactory;
import exceptions.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import views.RaceDetailsPanel;
import model.Model;
import model.RaceInfo;
import views.App;
import views.GhostSelectPanel;
import views.IResizeOberver;
import login.LoginPanel;
import views.RaceOverviewPanel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        Authenticator authenticator;
        boolean retry = true;
        while(retry){
            try {
                URL url = new URL("http://localhost:50248/races/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int status = con.getResponseCode();

                BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                retry = false;
            } catch (MalformedURLException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ProtocolException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        try {
            authenticator = new BasicAuthenticator(new ConnectionFactory());
            LoginResult result = authenticator.login(login, password);
            if(result.isLoggedIn()){
                showRaceOverview();
            }
            else{
                throw new LoginException(result.getErrorMessage());
            }
        } catch (Exception ex) {
            throw new LoginException(ex.getMessage());
        }
;
        
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
