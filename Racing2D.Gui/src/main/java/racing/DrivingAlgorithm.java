/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package racing;

import controller.Controller;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import model.GhostTraject;
import model.Model;
import model.positioning.Location;
import model.positioning.Orientation;
import model.positioning.Positioning;

/**
 *
 * @author Jaron
 */
public class DrivingAlgorithm implements ActionListener {

    private Car car;
    private Track track;
    private RacingPanel panel;
    private UserInputs inputs;
    
    private Timer timer;
    private GameTimer gameTimer;
   // private Stopwatch stopWatch;
    private boolean isFinishAvailable = true;
    public double laps;
    private GhostTraject ghostSave;
    private ArrayList<GhostTraject> ghosts;
    private Model model;
    private Controller controller;
    private BufferedImage ghostImage;

    public DrivingAlgorithm(int delay, Car car, Track track, RacingPanel panel, Model model, Controller controller, BufferedImage ghostImage, UserInputs inputs) {
        this.controller = controller;
        this.ghostImage = ghostImage;
        this.model=model;
        this.track = track;
        this.car = car;
        this.panel = panel;
        this.inputs = inputs;
        timer = new Timer(delay, this);
        gameTimer = new GameTimer(timer);
        laps = -1;
        ghostSave = new GhostTraject(0);
        setInitialCameraLocation();
        ghosts = model.getGhostTrajects();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int friction = track.getFriction((int)car.getX(),(int)car.getY());
        double timeDifference = gameTimer.getLastIntervalInMillis();
        
        
        if (inputs.isThrottlePressed()) {
            car.accelerate(timeDifference,friction);
        } else if (inputs.isBrakePressed()) {
            car.backwards(timeDifference,friction);
        } else {
            car.slowDown(timeDifference,friction);
        }
        
        if (inputs.isSteeringLeftPressed()) {
            car.turnCounterClockWise(timeDifference);
        } else if (inputs.isSteeringRightPressed()) {
            car.turnClockWise(timeDifference);
        } else {
            car.stopTurning(timeDifference);
        }
        
        
        Point[] cornerPixels = car.getCornerPixels(timeDifference, friction);
        if (track.isValidMove(cornerPixels)) {
            car.rotate(timeDifference, friction);
            car.translate(timeDifference);
            track.setX(car.getX());
            track.setY(car.getY());
        } else {
            car.setSpeedSize(0);
        }
        //TODO separate ghostSaver from ghostReader
        Positioning p = new Positioning(new Location((int) car.getX(),(int) car.getY()), new Orientation(car.getAngle()));
        ghostSave.addTick(p, (int) gameTimer.getTotalTimePassed());
        if (track.checkAtFinish(cornerPixels)) {
            if (isFinishAvailable) {
                handleLaps();
                isFinishAvailable = false;
            }
        } else {
            isFinishAvailable = true;
        }
        if (laps == track.getLaps()) {
            stop();
            handleFinish();
        }
        gameTimer.startNextInterval();
        panel.repaint();
    }
    
    public void displayGhosts(Graphics g) {
        for (int i=0; i<ghosts.size(); i++) {
            GhostTraject ghost = ghosts.get(i);
            int millis = (int) gameTimer.getTotalTimePassed();
            Point relativeLocation = ghost.getLocation(millis);
            int xTeken = (int)(relativeLocation.x - car.getX() + controller.getScreenWidth()/2);
            int yTeken = (int)(relativeLocation.y - car.getY() + controller.getScreenHeight()/2);
            double ghostAngle = ghost.getTheta(millis);
            Graphics2D g2 = (Graphics2D) g;
            double theta = -ghostAngle + Math.PI / 2;
            g2.translate(xTeken, yTeken);
            g2.rotate(theta);
            g2.translate(-ghostImage.getWidth()/2, -ghostImage.getHeight()/2);
            g2.drawImage(ghostImage, 0, 0, null);
            g2.translate(ghostImage.getWidth()/2, ghostImage.getHeight()/2);
            g2.rotate(-theta);
            g2.translate(-xTeken,-yTeken);
        }
    }
    
    public void start() {
        timer.start();
        gameTimer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void setInitialCameraLocation() {
        Point location = track.getAdjustedStartLocation();
        track.setY(-(location.y - controller.getScreenHeight() / 2));
        track.setX(-(location.x - controller.getScreenWidth() / 2));
        car.setY(location.y);
        car.setX(location.x);
    }

    private void handleFinish() {
        final int ms = (int) gameTimer.getTotalTimePassed();
        String message = "Uw tijd was " + ms+ "ms . Ghost opslaan?";
        int answer = JOptionPane.showConfirmDialog(panel, message, "Proficiat!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            SwingWorker worker = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    controller.saveAll(ghostSave, ms);
                    controller.saveTime(ms);
                    return null;
                }
                @Override
                public void done() {
                    controller.showRaceOverview();
                }
            };
            worker.execute();
            JOptionPane.showMessageDialog(panel,"Gelieve te wachten tot het traject opgeslaan is...","Even Geduld",JOptionPane.INFORMATION_MESSAGE);
        }else{
            controller.showRaceOverview();
        }
    }
    
    private void handleLaps() {
        double initialAngle = track.getStartAngle();
        double carAngle = car.getAngle();
        boolean kwadrant1 = (carAngle <= Math.PI/2 && carAngle >= 0) || (carAngle <= -3*Math.PI/2 && carAngle >= -2*Math.PI);
        boolean kwadrant2 = (carAngle >= Math.PI/2 && carAngle <= Math.PI) || (carAngle >= -3*Math.PI/2 && carAngle <= -Math.PI);
        boolean kwadrant3 = (carAngle >= Math.PI && carAngle <= 3*Math.PI/2) || (carAngle <= -Math.PI/2 && carAngle >= -Math.PI);
        boolean kwadrant4 = (carAngle >= -Math.PI/2 && carAngle <= 0) || (carAngle >= 3*Math.PI/2 && carAngle <= 2*Math.PI);
        if (car.getSpeedSize()>=0) {
            if (initialAngle == 0 && (kwadrant1 || kwadrant4)) {
                laps++;
            }
            else if (initialAngle == 0) {
                laps--;
            }
            if (initialAngle == Math.PI/2 && (kwadrant1 || kwadrant2)) {
                laps++;
            }
            else if (initialAngle == Math.PI/2) {
                laps--;
            }
            if (initialAngle == Math.PI && (kwadrant3 || kwadrant2)) {
                laps++;
            }
            else if (initialAngle == Math.PI) {
                laps--;
            }
            if (initialAngle == -Math.PI/2 && (kwadrant3 || kwadrant4)) {
                laps++;
            }
            else if (initialAngle == -Math.PI/2) {
                laps--;
            }
        }
        else {
            laps--;
        }
    }
    
}
