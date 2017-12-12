/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package racing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author lando
 */
public class UserInputs implements KeyListener{
    private boolean _throttlePressed;
    private boolean _brakePressed;
    private boolean _steeringLeftPressed;
    private boolean _steeringRightPressed;

    public boolean isThrottlePressed() {
        return _throttlePressed;
    }

    public boolean isBrakePressed() {
        return _brakePressed;
    }

    public boolean isSteeringLeftPressed() {
        return _steeringLeftPressed;
    }

    public boolean isSteeringRightPressed() {
        return _steeringRightPressed;
    }
    
    

    @Override
    public void keyTyped(KeyEvent e) {        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                _throttlePressed = true;
                break;
            case KeyEvent.VK_LEFT:
                _steeringLeftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                _steeringRightPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                _brakePressed = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                _throttlePressed = false;
                break;
            case KeyEvent.VK_LEFT:
                _steeringLeftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                _steeringRightPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                _brakePressed = false;
                break;
            default:
                break;
        }
    }
    
}
