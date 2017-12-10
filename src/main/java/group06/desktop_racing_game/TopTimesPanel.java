/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group06.desktop_racing_game;

import controller.Controller;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.Model;
import model.RaceInfo;
import model.Time;

/**
 * part of the RaceDetailsPanel
 * shows the world top times of a certain track
 * @author Lando
 */
public class TopTimesPanel extends JPanel{
    private final int TOP_N = 10;
    private Controller controller;
    private Model model;
    private RaceInfo race;
    private LayoutTable table;

    TopTimesPanel(Controller controller, Model model, RaceInfo race) {
        this.controller = controller;
        this.model = model;
        setRace(race);
    }

    private void setRace(RaceInfo race) {
        this.race = race;
        
        //copy the values of times into a format LayoutTable can handle: Object[][]
        ArrayList<Time> times = model.getTimes(TOP_N);
        String[] table_columns = {"player","time"};
        String[][] table_rows = new String[times.size()][];
        for (int i = 0; i < table_rows.length; i++) {
            String[] row = new String[table_columns.length];
            row[0] = times.get(i).getUsername();
            row[1] = times.get(i).toString();
            table_rows[i] = row;
        }
        
        //make the LayoutTable
        table = new LayoutTable(table_rows, table_columns);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                table.clearSelection(); //disable selecting cells
            }
        });
        add(table);
    }

}
