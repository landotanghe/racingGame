/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Point;
import java.util.ArrayList;

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
    private ArrayList<Point> locations;
    private ArrayList<Double> thetas;
    private ArrayList<Integer> millis;
    private int currentTick;

    public GhostTraject(int size) {
        locations = new ArrayList<Point>();
        thetas = new ArrayList<Double>();
        millis = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            locations.add(null);
            thetas.add(null);
            millis.add(null);
        }
        resetTick();
    }

    public void resetTick() {
        currentTick = -1;
    }

    public void addTick(int x, int y, double theta, int ms) {
        locations.add(new Point(x, y));
        millis.add(ms);
        thetas.add(theta);
    }

    public void addTick(int x, int y, double theta, int ms, int position) {
        locations.set(position, new Point(x, y));
        millis.set(position, ms);
        thetas.set(position, theta);
    }

    public boolean nextTick() {
        boolean hasNextTick = currentTick + 1 < locations.size();
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
        return locations.get(currentTick);
    }

    public int getX() {
        return (int) locations.get(currentTick).getX();
    }

    public int getY() {
        return (int) locations.get(currentTick).getY();
    }

    public double getTheta() {
        return thetas.get(currentTick);
    }

    public double getMillis() {
        return millis.get(currentTick);
    }

    public Point getLocation(int ms) {

        int t1 = getTickBeforeMs(ms);
        int t2 = getTickAfter(t1);
        Point p1 = locations.get(t1);
        if (t1 != t2) {
            Point p2 = locations.get(t1);
            int ms1 = millis.get(t1);
            int ms2 = millis.get(t2);
            double coeffX = (p1.x - p2.x + 0.0) / (ms1 - ms2);
            double coeffY = (p1.y - p2.y + 0.0) / (ms1 - ms2);
            return new Point((int) (p1.x + (ms - ms1) * coeffX), (int) (p1.y + (ms - ms2) * coeffY));
        } else {
            return new Point((int) p1.x, (int) p1.y);
        }
        //return new Point(locations.get(getTickBefore(ms)).x, locations.get(getTickBefore(ms)).y);
    }

    public double getTheta(int ms) {

        int t1 = getTickBeforeMs(ms);
        int t2 = getTickAfter(t1);
        double theta1 = thetas.get(t1);
        if (t1 != t2) {
            double theta2 = thetas.get(t1);
            int ms1 = millis.get(t1);
            int ms2 = millis.get(t2);
            double coeff = (theta1 - theta2 + 0.0) / (ms1 - ms2);
            return theta1 + (ms - ms1) * coeff;
        } else {
            return theta1;
        }
        //return thetas.get(getTickBefore(ms));

    }

    private int getTickBeforeMs(int ms) {
        int i = 0;
        while (i + 1 < millis.size() && millis.get(i + 1) < ms) {
            i++;
        }
        return i;
    }

    private int getTickAfter(int index) {
        if (index + 1 < thetas.size()) {
            index++;
        }
        return index;

    }

}
