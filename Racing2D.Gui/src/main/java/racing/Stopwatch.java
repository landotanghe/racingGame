/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package racing;

import java.awt.Font;
import java.awt.Graphics;

/**
 *
 * @author Jaron Couvreur
 */
public class Stopwatch {

    int x, y;
    int minutes, seconds;
    double hundreds;
    Font font;

    public Stopwatch(int x, int y, int minutes, int seconds, double hundreds, Font font) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.hundreds = hundreds;
        this.font = font;
        this.x = x;
        this.y = y;
    }

    public void display(Graphics g) {
        g.setFont(font);
        String time = getTimeString();
        g.drawString(time, x, y);
    }

    public String getTimeString() {
        String time = "";
        if (minutes <= 9) {
            time += "0";
        }
        time = time + minutes + ":";
        if (seconds <= 9) {
            time += "0";
        }
        time = time + seconds + ":";
        if (hundreds <= 9) {
            time += "0";
        }
        time += (int) hundreds;
        return time;
    }

    void addMillis(double m) {
        hundreds += m / 10;
        if (hundreds >= 100) {
            hundreds = hundreds - 100;
            seconds++;
        }
        if (seconds >= 60) {
            seconds = seconds - 60;
            minutes++;
        }
    }

    public int getMillis() {
        int totalHundreds = (int) (hundreds + seconds * 100 + minutes * 60 * 100);
        return totalHundreds * 10;
    }
}
