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
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.FormattedTile;
import model.GhostReplay;
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
    private Font stopWatchFont;
    private Model model;
    private Controller controller;
    private FormattedTile[][] tiles;
    private BufferedImage tilesImage;
    private Track track;
    private UserInputs inputs;
    
    public RacingPanel(Controller controller,Model model) {
        this.model = model;
        this.controller = controller;
        inputs = new UserInputs();
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
        algorithm = new DrivingAlgorithm(DELAY,car,track,this,model,controller,ghostImage, inputs, new ArrayList<GhostReplay>());
        algorithm.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        track.display(g);
        algorithm.displayGhosts(g);
        car.display(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        inputs.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        inputs.keyReleased(e);
    }

    public void keyTyped(KeyEvent e) {
    }
}
