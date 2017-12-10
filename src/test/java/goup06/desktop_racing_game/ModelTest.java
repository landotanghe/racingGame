/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package goup06.desktop_racing_game;

import exceptions.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import model.Model;

/**
 *
 * @author admin
 */
public class ModelTest extends TestCase {
    
    public ModelTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    public void testLoginFail(){
        String login = "!shouldfail";
        String password = "!shouldfail";
        Model model = new Model();
        try {
            model.login( login,  password);
            fail("should throw loginException");
        } catch (LoginException ex) {
        } catch (Exception ex){
            fail("should throw loginException");
        }
        
        
    }
    public void testLoginSucces(){
        
        String login = "Shirley";
        String password = "Temple";
        Model model = new Model();
        try {
            model.login( login,  password);
        } catch (LoginException ex) {
            fail("should work: can be temporary because of network problems");
        }
    }
}
