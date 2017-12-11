/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.undergrounds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author admin
 */
public class StandardUnderground implements IUnderground {
    
    //skins
    private BufferedImage background;
    private BufferedImage track;
    private BufferedImage finish;
    
    //reference rgb values of the image, to determine what skin should be applied
    private int trackRgb;
    private int finishRgb;
    private int backgroundRgb;

    public StandardUnderground() {
        try {
            background = ImageIO.read(new File("src/undergrounds/standard/background.png"));
            track = ImageIO.read(new File("src/undergrounds/standard/track.png"));
            finish = ImageIO.read(new File("src/undergrounds/standard/finish.png"));

            BufferedImage referenceImage = ImageIO.read(new File("src/tiles/colorReferences.png"));
            finishRgb = referenceImage.getRGB(1, 1);//-10912732;
            backgroundRgb = referenceImage.getRGB(50, 50);
            trackRgb = referenceImage.getRGB(80, 72); //-10912732;// 

        } catch (Exception exc) {
            Logger.getLogger(StandardUnderground.class.getName()).warning("not able to load undergrounds correctly");
        }
    }

    //geprobeerd rechtstreeks met color.white en dergelijke ==> FAIL
    //geprobeerd met referencebestand =>FAIL
    public void enhance(BufferedImage image) {
        Graphics g = image.createGraphics();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                try {
                    int imageRgb = image.getRGB(x, y);
                    int newRgb;
                    if (imageRgb == backgroundRgb) {
                        newRgb = background.getRGB(x, y);
                    } else if (imageRgb == finishRgb) {
                        newRgb = finish.getRGB(x, y);
                    } else if (imageRgb == trackRgb) {
                        newRgb = track.getRGB(x, y);
                    }else{
                        throw new Exception();
                    }
                    g.setColor(new Color(newRgb));
                    g.drawRect(x, y, 0, 0);
                } catch (Exception e) {
                   // Logger.getLogger(StandardUnderground.class.getName()).warning("could not process pixel (" + x + "," + y + ")");
                }
            }
        }
    }

}
