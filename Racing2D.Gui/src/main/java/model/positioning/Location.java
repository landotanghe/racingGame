/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.positioning;

import java.awt.Point;

/**
 *
 * @author lando
 */
public class Location {
    Point _point;

    public Location(Point point) {
        this._point = point;
    }

    public Location(int x, int y) {
        _point = new Point(x,y);
    }

    public Point getPoint() {
        return _point;
    }    
}
