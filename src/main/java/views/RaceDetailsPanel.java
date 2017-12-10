/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import controller.Controller;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Model;
import model.RaceInfo;

/**
 *  interface with user:
 *  shows all the info you need about a certain race: 
 *  race name
 *  your personnel best
 *  world top times (TopTimesPanel)
 * ...
 * @author Lando
 */
public class RaceDetailsPanel extends JPanel {

    TopTimesPanel topTimesPanel;
    JPanel buttonPanel;
    JPanel titlePanel;
    
    JButton startButton;
    JButton ghostsButton;
    JButton backButton;
    
    JLabel myBestLabel;
    JPanel bestPanel;

    Controller controller;
    Model model;
    RaceInfo race;

    public RaceDetailsPanel(Controller controller, Model model, RaceInfo race) {
        Logger.getLogger("RaceDetailsPanel").info("creating...");
        this.controller = controller;
        this.model = model;
        this.race = race;
        
        setLayout(new BorderLayout());
        
        titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(new JLabel(race.getName()));
        add(titlePanel, BorderLayout.NORTH);
        makeMyBestLabel();
        
        bestPanel = new JPanel();
        bestPanel.setLayout(new BorderLayout());
        bestPanel.add(myBestLabel);
        titlePanel.add(bestPanel,BorderLayout.SOUTH);

        makeStartButton();
        makeGhostsButton();
        makeBackButton();
        
        buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(ghostsButton);
        buttonPanel.add(backButton);
        titlePanel.add(buttonPanel);
        
        topTimesPanel = new TopTimesPanel(controller,model,race);
        add(topTimesPanel);
        

        Logger.getLogger("RaceDetailsPanel").info("...created");
    }
    
    private void makeStartButton(){
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.start(race);
            }
        });
    }
    private void makeGhostsButton(){
        ghostsButton = new JButton("Select ghosts");
        ghostsButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //controller
                controller.showGhostsPanel(race);
            }
        });
    }
     private void makeBackButton(){
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.showRaceOverview();
            }
        });
    }

    private void makeMyBestLabel() {
        String best = model.getPersonelBestTime().toString();
        if(best != ""){
            best = " your best: " + best;
        }else{
            best =" you haven't set a time yet";
        }
        myBestLabel = new JLabel(best);
    }
}
