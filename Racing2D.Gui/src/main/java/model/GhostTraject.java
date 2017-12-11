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
public class GhostTraject {

    //lees en schrijf ticks niet afwisselend, doe opeenvolgende leesoperaties na elkaar, net als schrijfoperaties
    //vergeet niet resetTick() op te roepen om ticks te herschrijven of te herlezen
    // private String username;
    private ArrayList<Integer> millis;
    private ArrayList<Positioning> positions;
    private int currentTick;

    public GhostTraject(int size) {
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
    
    public Point getLocation(int ms) {
       return getPositioning(ms).getLocation().getPoint();
    }

    public Positioning getPositioning(int ms) {

        int t1 = getTickBeforeMs(ms);
        int t2 = getTickAfter(t1);
        Positioning p1 = positions.get(t1);
        if (t1 != t2) {
            return getAveragePosition(t1, t2, ms, p1);
        } else {
            return p1;
        }
        //return new Point(locations.get(getTickBefore(ms)).x, locations.get(getTickBefore(ms)).y);
    }

    public Positioning getAveragePosition(int t1, int t2, int ms, Positioning p1) {
        Positioning p2 = positions.get(t1);
        int ms1 = millis.get(t1);
        int ms2 = millis.get(t2);
        
        double coeff = (ms - ms1) / (ms1 - ms2);
        
        double deltaX = (p1.getX() - p2.getX() + 0.0);
        double deltaY = (p1.getY() - p2.getY() + 0.0);
        double deltaTheta = (p1.getOrientation().getTheta() - p2.getOrientation().getTheta());
        
        int x = (int) (p1.getLocation().getPoint().x + coeff * deltaX);
        int y = (int) (p1.getLocation().getPoint().y + coeff * deltaY);
        double theta = p1.getOrientation().getTheta() + coeff * deltaTheta;
        
        Location location = new Location(new Point(x,y));
        Orientation orientation = new Orientation(theta);
        
        return new Positioning(location, orientation);
    }

    public double getTheta(int ms) {
       return getPositioning(ms).getOrientation().getTheta();
    }

    private int getTickBeforeMs(int ms) {
        int i = 0;
        while (i + 1 < millis.size() && millis.get(i + 1) < ms) {
            i++;
        }
        return i;
    }

    private int getTickAfter(int index) {
        if (index + 1 < positions.size()) {
            index++;
        }
        return index;
    }

}
