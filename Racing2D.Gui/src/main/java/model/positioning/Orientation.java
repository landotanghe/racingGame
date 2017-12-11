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
public class Orientation {
    public double _angleWithNorthAxis;
    
    public Orientation(double angleWithNorthAxis){
        _angleWithNorthAxis = angleWithNorthAxis;
    }
    
    public double getTheta(){
        return _angleWithNorthAxis;
    }
}
