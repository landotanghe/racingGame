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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.Model;
import model.RaceInfo;

/**
 * interface with user:
 * user can select a race he wants to play or see the details for.
 * @author Lando
 */

public class RaceOverviewPanel extends JPanel {
    private final int AANTAL_PER_PAGINA = 50;

    
    Controller controller;
    Model model;
    final private String[] columnNames = {"number", "trackname", "creator"};
    LayoutTable table;
    JPanel buttonPanel;
    private ArrayList<RaceInfo> races;
    JButton start, details, exit;

    public RaceOverviewPanel(Controller c, Model m) {
        controller = c;
        model = m;
        setLayout(new BorderLayout());
        buttonPanel = new JPanel();
        start = makeStartButton();
        details = makeDetailsButton();
        exit = makeExitButton();
        buttonPanel.add(start);
        buttonPanel.add(details);
        buttonPanel.add(exit);
        add(buttonPanel, BorderLayout.NORTH);
    }

    public void fill() {
        races = model.getRaceList(AANTAL_PER_PAGINA);
        Object rows[][] = new Object[races.size()][columnNames.length];
        for (int i = 0; i < races.size(); i++) {
            int number = races.get(i).getId();
            String trackName = races.get(i).getName();
            String creatorName = races.get(i).getCreatorName();

            Object[] row = new Object[]{number, trackName, creatorName};
            rows[i] = row;
        }
        table = new LayoutTable(rows, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                start.setEnabled(true);
                details.setEnabled(true);
            }
        });
        add(table, BorderLayout.CENTER);
        start.setEnabled(false);
        details.setEnabled(false);
    }

    private JButton makeStartButton() {
        JButton start = new JButton("Quick start");
        //actionlistener
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.start(races.get(table.getSelectedRow()));
            }
        });
        return start;
    }

    private JButton makeDetailsButton() {
        JButton details = new JButton("Details");
        details.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.showRaceDetails(races.get(table.getSelectedRow()));
            }
        });
        //actionlisteners
        return details;
    }
    
    private JButton makeExitButton() {
        JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        //actionlisteners
        return exit;
    }
}
