/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package racing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author lando
 */
public class GameTimer implements ActionListener{
    private Timer timer;
    private double timerStarted;
    private double lastIntervalStopped;
    private double lastIntervalStarted;
    
    
    public GameTimer(Timer timer) {
        this.timer = timer;
    }
    
    public void start(){
        lastIntervalStarted = System.currentTimeMillis();
        lastIntervalStopped = System.currentTimeMillis();
    }
    
    public void startNextInterval(){
        lastIntervalStarted = lastIntervalStopped;
        lastIntervalStopped = System.currentTimeMillis();
    }
    
    public double getLastIntervalInMillis(){
        return lastIntervalStopped - lastIntervalStarted;
    }
    
    public double getTotalTimePassed(){
        return lastIntervalStopped - timerStarted;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
