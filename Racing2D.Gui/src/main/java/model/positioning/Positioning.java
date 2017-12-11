/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.positioning;

/**
 *
 * @author lando
 */
public class Positioning {
    private final Orientation _orientation;
    private final Location _location;

    public Positioning(Location location, Orientation orientation) {
        _location = location;
        _orientation = orientation;
    }

    public Location getLocation() {
        return _location;
    }
    
    public double getX(){
        return _location.getPoint().getX();
    }
    
    public double getY(){
        return _location.getPoint().getY();
    }
    
    public Orientation getOrientation() {
        return _orientation;
    }    
}
