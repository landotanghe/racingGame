/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 */
package racing;

import exceptions.DrivingException;
import controller.Controller;
import group06.desktop_racing_game.IResizeOberver;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jaron
 */
public class Car implements IResizeOberver {

    private int halfScreenWidth, halfScreenHeight;
    private int mass = 1500;

    private BufferedImage image;
    private double x, y;
    private Speed speed;
    private int acceleration;
    private int accelerationBackwards;
    private double accelerationSlowing;
    private int width, height;
    private double topSpeed;
    private double topSpeedBackwards;

    private int numberOfWheelTurns = 0;
    public int MaxNumberOfWheelTurns = 200; //aantal draaiien vanuit wheel_hoek=0 om max wheel angle te bereiken
    public static final double MAX_WHEEL_ANGLE = 3; //max hoeksnelheid vd auto in graden, later afh van snelheid?

    private Controller controller;
    private int minAcceleration;

    public Car(BufferedImage image, double initialSpeed, double initialAngle, double topSpeed, int acceleration, Controller controller) throws DrivingException { //hoek volgens goniometrische cirkel
        this.controller = controller;
        if (topSpeed < 0) {
            throw new DrivingException("TopSpeed must be greater than 0");
        }
        if (initialSpeed > topSpeed) {
            throw new DrivingException("InitialSpeed must be less than TopSpeed");
        }
        if (acceleration < 0) {
            throw new DrivingException("Acceleration must be greater than 0");
        }
        if (image == null) {
            throw new DrivingException("No car image");
        }
        speed = new Speed(initialSpeed, initialAngle);
        this.acceleration = acceleration;
        accelerationBackwards = acceleration / 2;
        accelerationSlowing = acceleration / 3;
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        this.topSpeed = topSpeed;
        topSpeedBackwards = topSpeed / 2;

        controller.addResizeObserver(this);
        halfScreenHeight = controller.getScreenHeight() / 2;
        halfScreenWidth = controller.getScreenWidth() / 2;
        minAcceleration = 50;
    }

    public double getAngle() {
        return speed.getAngle();
    }

    public void display(Graphics g) {
        
        //poging 3: midden auto is draaias:
        Graphics2D g2 = (Graphics2D) g;
        g.translate(halfScreenWidth, halfScreenHeight);
        double theta = speed.getAngle() - Math.PI / 2; // Pi/2 om de neus van de auto juist te plaatsen
        g2.rotate(-theta);
        g2.translate(-width/2, -height/2);
        g2.drawImage(image, 0, 0, null);
        g2.translate(width/2, height/2);
        g2.rotate(theta);
        g.translate(-halfScreenWidth, -halfScreenHeight);
    }

    public Point[] getCornerPixels(double t, double friction) {
        //geeft de hoeken van de auto als hij zich zou verplaatsen
        //zodat kan nagegaan worden of een geldige beweging zal worden gedaan t.o.v. de track
        Point[] pixels = new Point[4];
        rotate(t, friction);
         translate(t);

         //onverdraaide coordinaten
         double[] xOrigineel = new double[4];
         double[] yOrigineel = new double[4];
         xOrigineel[0] = -width / 2;
         yOrigineel[0] = -height / 2;

         xOrigineel[1] = -width / 2;
         yOrigineel[1] = height / 2;

         xOrigineel[2] = width / 2;
         yOrigineel[2] = -height / 2;

         xOrigineel[3] = width / 2;
         yOrigineel[3] = height / 2;
         double angle = speed.getAngle() + Math.PI/2;
         double sin = Math.sin(angle);
         double cos = Math.cos(angle);
         //assenstels draaien indien rechtsdraaiiend:
         //  x' = x cos(theta) - y sin(theta)
         //  y' = x sin(theta) + y cos(theta)
         //hier beide assenstelsels linksdraaiiend, dus y <-> - y  en y' <-> -y'
         //  x' = x cos(theta) + y sin(theta)
         //  -y' = x sin(theta) - y cos(theta)  => y' = -x sin(theta) + y cos(theta)
         double xAccent[] = new double[4];
         double yAccent[] = new double[4];
         for (int i = 0; i < xAccent.length; i++) {
         xAccent[i] =  xOrigineel[i] * cos + yOrigineel[i] * sin;
         yAccent[i] = - xOrigineel[i] * sin + yOrigineel[i] * cos;
         pixels[i] = new Point((int) (x + xAccent[i]), (int) (y + yAccent[i]));
         }
         translate(-t);
         rotate(-t, friction);
         
        return pixels;

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return speed.getX();
    }

    public double getVy() {
        return speed.getY();
    }

    public void setSpeedSize(double grootte) {
        speed.setSize(grootte);
    }

    public void setSpeedAngle(double hoek) {
        speed.setAngle(hoek);
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public double getSpeedSize() {
        return speed.getSize();
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public void accelerate(double t, int friction) { //increase speed unless already topSpeed
  
        
        //http://en.wikipedia.org/wiki/Tractive_force
        //the maximum force that can exist between car and road = friction force
        int a = accelerationBackwards ;
       /* if(a > friction){
            a = friction;
        }*/
        
        double nextSpeed = (speed.getSize() + a * t / 1000);
        if (nextSpeed > topSpeed) {
            speed.setSize(topSpeed);
        } else {
            speed.setSize(nextSpeed);
        }
    }

    public void slowDown(double t, int friction) { //decrease/increase speed towards 0
        double v = speed.getSize();
        int a = friction;
        if (v > 0) {
            double nextSpeed = v - a * t / 1000;
            if (nextSpeed < 0) {
                speed.setSize(0);
            } else {
                speed.setSize(nextSpeed);
            }
        } else {
            double nextSpeed = v + a * t / 1000;
            if (nextSpeed > 0) {
                speed.setSize(0);
            } else {
                speed.setSize(nextSpeed);
            }

        }
    }

    public void backwards(double t, int friction) {// accelerate backwards unless - topSpeed reached , can be used as break
        
        
        int a = accelerationBackwards ;
        /*if(a > friction){
            a = friction;
        }*/
        
        double nextSpeed = (speed.getSize() - a * t / 1000);
        if (nextSpeed < -topSpeedBackwards) {
            speed.setSize(-topSpeedBackwards);
        } else {
            speed.setSize(nextSpeed);
        }
    }

    public void turnClockWise(double t) {
        numberOfWheelTurns -= t;
        if (numberOfWheelTurns < -MaxNumberOfWheelTurns) {
            numberOfWheelTurns = -MaxNumberOfWheelTurns;
        }
    }

    public void turnCounterClockWise(double t) {
        numberOfWheelTurns += t;
        if (numberOfWheelTurns > MaxNumberOfWheelTurns) {
            numberOfWheelTurns = MaxNumberOfWheelTurns;
        }
    }

    public void stopTurning(double t) {//stapsgewijs draaihoek terug naar 0 zetten
        if (numberOfWheelTurns < 0) {
            numberOfWheelTurns += t;
            if (numberOfWheelTurns > 0) {
                numberOfWheelTurns = 0;
            }
        } else if (numberOfWheelTurns > 0) {
            numberOfWheelTurns -= t;
            if (numberOfWheelTurns < 0) {
                numberOfWheelTurns = 0;
            }
        }
    }

    public void rotate(double t, double friction) {
        double v = speed.getSize();
        if (v != 0) {//otherwise divide by zero
            //double theta_max = Math.asin(t / v / friction);
            //double theta = numberOfWheelTurns / MaxNumberOfWheelTurns * theta_max;
            // speed.rotate(theta);
            
            /*
            double theta_max = Math.asin(t / 2000 / v / friction * mass);
            System.out.println(theta_max);
            double theta = numberOfWheelTurns / MaxNumberOfWheelTurns * theta_max;
            speed.rotate(theta);
            */
            double theta = numberOfWheelTurns * MAX_WHEEL_ANGLE / MaxNumberOfWheelTurns;
            speed.rotate(theta * t / 1000);

        }
    }

    public void translate(double t) {
        x += speed.getX() * t / 1000;
        y += speed.getY() * t / 1000;
    }

    public void update(int width, int height) {
        halfScreenWidth = width / 2;
        halfScreenHeight = height / 2;
    }

}
