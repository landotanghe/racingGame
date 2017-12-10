/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package racing;

import controller.Controller;
import group06.desktop_racing_game.IResizeOberver;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import model.FormattedTile;
import model.Model;

/**
 *
 * @author Jaron
 */
public class Track implements IResizeOberver {

    private int halfScreenWidth, halfScreenHeight;
    private FormattedTile[][] tiles;
    private BufferedImage graphicsImage;
    private BufferedImage logicImage;
    private double x, y;
    private int laps = 1;
    private Controller controller;
    private Model model;

    public Track(Controller controller, Model model, FormattedTile[][] tiles) {
        this.controller = controller;
        this.model = model;
        this.tiles = tiles;
        x = 0;
        y = 0;
        createTrackImage(tiles);
        controller.addResizeObserver(this);
        halfScreenHeight = controller.getScreenHeight() / 2;
        halfScreenWidth = controller.getScreenWidth() / 2;
    }

    public int getLaps() {
        return laps;
    }

    private void createTrackImage(FormattedTile[][] tiles) {
        int tileWidth = tiles[0][0].getWidth();
        int tileHeight = tiles[0][0].getHeight();
        graphicsImage = new BufferedImage(tiles.length * tileWidth, tiles.length * tileHeight, BufferedImage.TYPE_3BYTE_BGR); //TYPE????, HARDGECODEERD
        logicImage = new BufferedImage(tiles.length * tileWidth, tiles.length * tileHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = graphicsImage.createGraphics();
        Graphics g2 = logicImage.createGraphics();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                tiles[i][j].drawClassic(g2, j * tileWidth, i * tileHeight);
                tiles[i][j].drawFormatted(g, j * tileWidth, i * tileHeight);
            }
        }
    }

    public Point getAdjustedStartLocation() {
        int tileWidth = tiles[0][0].getWidth();
        int tileHeight = tiles[0][0].getHeight();
        int startX = 0;
        int startY = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j].isStartUp()) {
                    startX = tileWidth * j + tileWidth / 2;
                    startY = tileHeight * i + tileHeight / 2 + 11;
                }
                if (tiles[i][j].isStartRight()) {
                    startX = tileWidth * j + tileWidth / 2 - 10;
                    startY = tileHeight * i + tileHeight / 2;
                }
                if (tiles[i][j].isStartDown()) {
                    startX = tileWidth * j + tileWidth / 2;
                    startY = tileHeight * i + tileHeight / 2 - 7;
                } else if (tiles[i][j].isStartLeft()) {
                    startX = tileWidth * j + tileWidth / 2 + 10;
                    startY = tileHeight * i + tileHeight / 2;
                }
            }
        }
        return new Point(startX, startY);
    }

    public Point getStartLocation() {
        int tileWidth = tiles[0][0].getWidth();
        int tileHeight = tiles[0][0].getHeight();
        int startX = 0;
        int startY = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j].isStartUp()) {
                    startX = tileWidth * j + tileWidth / 2;
                    startY = tileHeight * i + tileHeight / 2;
                }
                if (tiles[i][j].isStartRight()) {
                    startX = tileWidth * j + tileWidth / 2;
                    startY = tileHeight * i + tileHeight / 2;
                }
                if (tiles[i][j].isStartDown()) {
                    startX = tileWidth * j + tileWidth / 2;
                    startY = tileHeight * i + tileHeight / 2;
                } else if (tiles[i][j].isStartLeft()) {
                    startY = tileHeight * i + tileHeight / 2;
                    startX = tileWidth * j + tileWidth / 2;
                }
            }
        }
        return new Point(startX, startY);
    }

    public double getStartAngle() {
        double angle = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j].isStartUp()) {
                    angle = Math.PI / 2;
                } else if (tiles[i][j].isStartRight()) {
                    angle = 0;
                } else if (tiles[i][j].isStartDown()) {
                    angle = -Math.PI / 2;
                } else if (tiles[i][j].isStartLeft()) {
                    angle = Math.PI;
                }
            }
        }
        return angle;
    }

    public void display(Graphics g) {
        g.drawImage(graphicsImage, halfScreenWidth - (int) x, halfScreenHeight - (int) y, null);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private int getRoadColor() {
        Point start = getStartLocation();
        return logicImage.getRGB(start.x - 5, start.y - 5);
    }

    private int getFinishLineColor() {
        Point start = getStartLocation();
        return logicImage.getRGB(start.x, start.y);
    }

    public boolean isValidMove(Point[] cornerPixels) {
        int roadColor = getRoadColor();
        int finishLineColor = getFinishLineColor();
        for (int i = 0; i < 4; i++) {
            //Logger.getLogger(Track.class.getName()).info(cornerPixels[i].x +"," +cornerPixels[i].y);
            try {
                if (logicImage.getRGB(cornerPixels[i].x, cornerPixels[i].y) != roadColor && logicImage.getRGB(cornerPixels[i].x, cornerPixels[i].y) != finishLineColor) {
                    return false;
                }
            } catch (Exception ex) { // out of bounds is not allowed
                return false;
                //Logger.getLogger(Track.class.getName()).severe(cornerPixels[i].x +"," +cornerPixels[i].y);
            }
        }
        return true;
    }

    public boolean checkAtFinish(Point[] cornerPixels) {
        double finishColor = getFinishLineColor();
        boolean isAtFinish = false;
        for (int i = 0; i < 1; i++) {
            if (logicImage.getRGB(cornerPixels[i].x, cornerPixels[i].y) == finishColor) {
                isAtFinish = true;
            }
        }
        return isAtFinish;
    }

    public void update(int width, int height) {
        halfScreenHeight = height / 2;
        halfScreenWidth = width / 2;
    }

    int getFriction(int x, int y) {
        if (logicImage.getRGB(x, y) == getRoadColor() || logicImage.getRGB(x, y) == getFinishLineColor()) {
            return 50;
        } else {
            return 900;
        }
    }
}
