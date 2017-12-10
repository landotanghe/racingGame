/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package group06.desktop_racing_game;

import controller.Controller;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.GhostInfo;
import model.Model;

/**
 *
 * interface with user, user is able to select 0,1 or more ghosts that
 * he wants to see while racing
 *  
 * @author Lando
 */
public class GhostSelectPanel extends JPanel{
    
    private Controller controller;
    private Model model;
    ArrayList<GhostInfo> ghosts;
    
    private JPanel ghostsPanel;
    private JPanel buttonPanel;
    
    private JTable table;
    private JButton okButton;
    private JButton cancelButton;
    
    final private String[] columnNames = {"gid", "pilot", "time"};
    
    private ArrayList<Integer> gids;
    

    public GhostSelectPanel(Controller controller, Model model) {
        this.controller=controller;
        this.model=model;
        gids = new ArrayList<Integer>();
        
        setLayout(new BorderLayout());
        ghostsPanel = new JPanel();
        buttonPanel = new JPanel();
        makeOkButton();
        makeCancelButton();
        
        add(ghostsPanel);
        add(buttonPanel,BorderLayout.SOUTH);
        
    }
    public void makeOkButton(){
        okButton = new JButton("ok");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.selectGhosts(gids);
            }
        });
        buttonPanel.add(okButton);
    }
    
    public void makeCancelButton(){
        cancelButton = new JButton("cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.showRaceDetails(model.getRaceInfo());
            }
        });
        buttonPanel.add(cancelButton);
    }
    
    public void fill(){
        ghosts = model.getGhostInfos();
        Object rows[][] = new Object[ghosts.size()][columnNames.length];
        for (int i = 0; i < ghosts.size(); i++) {
            int gid = ghosts.get(i).getGid();
            String pilot = ghosts.get(i).getPilot();
            String time = ghosts.get(i).getTime();

            Object[] row = new Object[]{gid, pilot, time};
            rows[i] = row;
        }
        table = new JTable(rows, columnNames);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                gids = new ArrayList<Integer>();
                int[] selection = table.getSelectedRows();
                for(int i=0 ; i< selection.length;i++){
                    gids.add(ghosts.get(selection[i]).getGid());
                }
            }
        });
        ghostsPanel.add(table, BorderLayout.CENTER);
    }
    
}
