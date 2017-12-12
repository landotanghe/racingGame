/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Point;
import java.util.ArrayList;
import model.positioning.Location;
import model.positioning.Orientation;
import model.positioning.Positioning;

/**
 * used to temporary store the ticks to record the user's ghost or used as a
 * local copy from a ghost of the database to show to the user
 *
 *
 * @author Lando
 */
public class GhostTracker {

    //lees en schrijf ticks niet afwisselend, doe opeenvolgende leesoperaties na elkaar, net als schrijfoperaties
    //vergeet niet resetTick() op te roepen om ticks te herschrijven of te herlezen
    // private String username;
    private ArrayList<Integer> millis;
    private ArrayList<Positioning> positions;
    private int currentTick;

    public GhostTracker(int size) {
        positions = new ArrayList<Positioning>();
        millis = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            positions.add(null);
            millis.add(null);
        }
        resetTick();
    }

    public void resetTick() {
        currentTick = -1;
    }

    public void addTick(Positioning p, int ms) {
        positions.add(p);
        millis.add(ms);
    }

    public void addTick(Positioning p, int ms, int position) {
        positions.set(position, p);
        millis.set(position, ms);
    }

    public boolean nextTick() {
        boolean hasNextTick = currentTick + 1 < positions.size();
        if (hasNextTick) {//keep giving the last tick if no more ticks left in the recording
            currentTick++;
        }
        return hasNextTick;
    }/*
     public Point getLocation(int ms){
     Point calculated;
        
     return calculated;        
     }*/


    public Point getLocation() {
        return positions.get(currentTick).getLocation().getPoint();
    }

    public int getX() {
        return (int) getLocation().getX();
    }

    public int getY() {
        return (int) getLocation().getY();
    }

    public double getTheta() {
        return positions.get(currentTick).getOrientation().getTheta();
    }

    public double getMillis() {
        return millis.get(currentTick);
    }
}
