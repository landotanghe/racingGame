/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import model.undergrounds.IUnderground;

/**
 * tile that can be drawn in 2 different ways:
 * -for logic: drawClassic()
 * -for view: drawFormatted()
 * 
 * @author admin
 */
public class FormattedTile {

    //made by model
    //used by racingPanel 
    private BufferedImage tilebase;
    private String tilename;
    private IUnderground underground;
    private BufferedImage image;
    private String tilecode;

    public FormattedTile(String tilename,String tilecode, IUnderground underground) throws FileNotFoundException {
        this.underground = underground;
        this.tilename = tilename;
        this.tilecode = tilecode;
        //image kopieren van tile
        String fileLocation = "src/tiles/" + tilename;
        try {
            tilebase = ImageIO.read(new File(fileLocation));
        } catch (IOException ex) {
            Logger.getLogger(FormattedTile.class.getName()).warning("could not find " + fileLocation);
            throw new FileNotFoundException("could not find " + fileLocation);
        }
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.createGraphics();
        g.drawImage(tilebase, 0, 0, null);

        //undergrounds opplakken/construeren
        underground.enhance(image);
    }

    public void drawClassic(Graphics g, int x, int y) {
        //draws a tile in simple format: grey on white
        //used by racing logic: scan if car is still on track
        //                                  or crosses start
        g.drawImage(tilebase, x, y, null);
    }

    public void drawFormatted(Graphics g, int x, int y) {
        //draws a tile in colored format
        g.drawImage(image, x, y, null);
    }

    public int getHeight() {
        return 256;
    }

    public int getWidth() {
        return 256;
    }

    public boolean isStartRight(){
        return tilecode.equals("SHR");
    }
    public boolean isStartLeft(){
        return tilecode.equals("SHL");
    }
    public boolean isStartDown(){
        return tilecode.equals("SVO");
    }
    public boolean isStartUp(){
        return tilecode.equals("SVB");
    }

}
