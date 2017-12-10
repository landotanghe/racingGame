/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package racing;

/**
 *
 * @author Jaron
 */
public class Speed {

    private double size, angle; //alle hoeken in radialen

    public Speed(double grootte, double hoek) {
        this.size = grootte;
        this.angle = hoek;
    }

    public double getX() {
        return Math.cos(angle) * size;
    }

    public double getY() {
        return -Math.sin(angle) * size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        this.angle %= (2 * Math.PI);
    }

    private void incrementAngle(double extra) {
        angle += extra;
        angle %= (2 * Math.PI);//voorkomt overflow als blijft draaiien en is duidelijker
    }

    public double getSize() {
        return size;
    }

    public double getAngle() {
        return angle;
    }

    void rotate(double d) {
        if(size < 0){
            incrementAngle(-d);
        }else{
            incrementAngle(d);
        }
    }

}
