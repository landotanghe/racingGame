/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package views;

import java.awt.Color;
import java.awt.Font;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * a table that is used in all overviews in the game
 * so that they all look the same
 * 
 * @author admin
 */
public class LayoutTable extends JTable{

    LayoutTable(Object[][] rows, String[] columnNames) {
        super(rows,columnNames);
        setFont(new Font("Serif", Font.BOLD, 15));
        setBackground(new Color(200,255,255));
    }

    
}
