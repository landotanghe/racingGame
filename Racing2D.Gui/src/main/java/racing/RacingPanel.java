/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package racing;

import exceptions.DrivingException;
import controller.Controller;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.FormattedTile;
import model.Model;

/**
 *
 * @author Jaron
 */

//niet vergeten requestFocus() uit te voeren op het RacingPanel als de keyListener moet werken

public class RacingPanel extends JPanel implements KeyListener {
    private static final int DELAY=10;
    private Car car;
    private DrivingAlgorithm algorithm;
    private Stopwatch stopWatch;
    private Font stopWatchFont;
    private Model model;
    private Controller controller;
    private FormattedTile[][] tiles;
    private BufferedImage tilesImage;
    private Track track;
    
    public RacingPanel(Controller controller,Model model) {
        this.model = model;
        this.controller = controller;
        tiles = model.getRaceTrack();
        track = new Track(controller,model,tiles);
        setFocusable(true);
        addKeyListener(this);
        BufferedImage carImage = null;
        BufferedImage ghostImage = null;
        try {
            carImage = ImageIO.read(new File("src/car.png"));
            ghostImage = ImageIO.read(new File("src/ghost.png"));
        } catch (Exception exc) {
            Logger.getLogger(RacingPanel.class.getName()).warning("image Car and/or Ghost was not found");
        }
        double angle = track.getStartAngle();
        try {
            car = new Car(carImage,0,angle,350,600,controller);
        }
        catch(DrivingException exc) {
            System.err.println(exc.getMessage());
        }
        stopWatchFont = new Font("Arial",Font.BOLD,24);
        stopWatch = new Stopwatch(20,30,0,0,0,stopWatchFont);
        algorithm = new DrivingAlgorithm(DELAY,car,track,this,stopWatch,model,controller,ghostImage);
        algorithm.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        track.display(g);
        stopWatch.display(g);
        algorithm.displayGhosts(g);
        car.display(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP ) {
            algorithm.upKeyPressed();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            algorithm.leftKeyPressed();
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            algorithm.rightKeyPressed();
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            algorithm.downKeyPressed();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP ) {
            algorithm.upKeyReleased();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            algorithm.leftKeyReleased();
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            algorithm.rightKeyReleased();
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            algorithm.downKeyReleased();
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}
