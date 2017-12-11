/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model.undergrounds;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * used to make FormattedTiles better looking
 * every possible color on the basic image( logic image) should be replaced by a skin,
 * this includes : starting line,
 *                 road color,
 *                 background color
 * 
 * 
 * @author admin
 */
public interface IUnderground {
    
    public void enhance(BufferedImage image);
}
