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

    private Timer timer;
    private Car car;
    private Track track;
    private RacingPanel panel;
    private boolean isKeyUp = false;
    private boolean isKeyLeft = false;
    private boolean isKeyRight = false;
    private boolean isKeyDown = false;
    private Stopwatch stopWatch;
    private double previousTime, currentTime;
    private boolean isFinishAvailable = true;
    public double laps;
    private GhostTraject ghostSave;
    private ArrayList<GhostTraject> ghosts;
    private Model model;
    private Controller controller;
    private BufferedImage ghostImage;

    public DrivingAlgorithm(int delay, Car car, Track track, RacingPanel panel, Stopwatch stopwatch, Model model, Controller controller, BufferedImage ghostImage) {
        this.controller = controller;
        this.ghostImage = ghostImage;
        this.model=model;
        this.track = track;
        this.car = car;
        this.panel = panel;
        timer = new Timer(delay, this);
        stopWatch = stopwatch;
        laps = -1;
        ghostSave = new GhostTraject(0);
        setInitialCameraLocation();
        ghosts = model.getGhostTrajects();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int friction = track.getFriction((int)car.getX(),(int)car.getY());
        currentTime = System.currentTimeMillis();
        double timeDifference = currentTime - previousTime;
        stopWatch.addMillis(timeDifference);
        if (isKeyUp) {
            car.accelerate(timeDifference,friction);
        } else if (isKeyDown) {
            car.backwards(timeDifference,friction);
        } else {
            car.slowDown(timeDifference,friction);
        }
        if (isKeyLeft) {
            car.turnCounterClockWise(timeDifference);
        } else if (isKeyRight) {
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
        ghostSave.addTick(p, stopWatch.getMillis());
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
        previousTime = currentTime;
        panel.repaint();
    }
    
    public void displayGhosts(Graphics g) {
        for (int i=0; i<ghosts.size(); i++) {
            GhostTraject ghost = ghosts.get(i);
            int millis = stopWatch.getMillis();
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
        previousTime = System.currentTimeMillis();
    }

    public void stop() {
        timer.stop();
    }

    void upKeyPressed() {
        isKeyUp = true;
    }

    void leftKeyPressed() {
        isKeyLeft = true;
    }

    void rightKeyPressed() {
        isKeyRight = true;
    }

    void downKeyPressed() {
        isKeyDown = true;
    }

    void upKeyReleased() {
        isKeyUp = false;
    }

    void leftKeyReleased() {
        isKeyLeft = false;
    }

    void rightKeyReleased() {
        isKeyRight = false;
    }

    void downKeyReleased() {
        isKeyDown = false;
    }

    private void setInitialCameraLocation() {
        Point location = track.getAdjustedStartLocation();
        track.setY(-(location.y - controller.getScreenHeight() / 2));
        track.setX(-(location.x - controller.getScreenWidth() / 2));
        car.setY(location.y);
        car.setX(location.x);
    }

    private void handleFinish() {
        String message = "Uw tijd was " + stopWatch.getTimeString() + ". Ghost opslaan?";
        int answer = JOptionPane.showConfirmDialog(panel, message, "Proficiat!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            SwingWorker worker = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    controller.saveAll(ghostSave, stopWatch.getMillis());
                    controller.saveTime(stopWatch.getMillis());
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
