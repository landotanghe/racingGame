/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import junit.framework.TestCase;
import model.GhostTraject;

/**
 *
 * @author Lando
 */
public class GhostTrajectTest extends TestCase {

    public GhostTrajectTest(String testName) {
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
    public void testEmpty() {
        GhostTraject ghost = new GhostTraject(0);
        assertFalse(ghost.nextTick());
        ghost.addTick(10, 10, 3.2,0);
        ghost.addTick(8, 6, 3.2,1);

    }

    public void testFill() {

        GhostTraject ghost = new GhostTraject(0);
        int x[] = {10, 8};
        int y[] = {10, 6};
        double theta[] = {3.2, 6.8};
        for (int i = 0; i < x.length; i++) {
            ghost.addTick(x[i], y[i], theta[i],0);
        }
        ghost.resetTick();
        int i = 0;
        while (ghost.nextTick()) {
            assertEquals(ghost.getX(), x[i]);
            assertEquals(ghost.getY(), y[i]);
            assertEquals(ghost.getTheta(), theta[i]);
            i++;
        }
        assertEquals(i, 2);

    }
    public void testToFull(){
        
        GhostTraject ghost = new GhostTraject(0);
        int x[] = {10, 8};
        int y[] = {10, 6};
        double theta[] = {3.2, 6.8};
        for (int i = 0; i < x.length; i++) {
            ghost.addTick(x[i], y[i], theta[i],0);
        }
        ghost.resetTick();
        int i = 0;
        while (ghost.nextTick()) {
            assertEquals(ghost.getX(), x[i]);
            assertEquals(ghost.getY(), y[i]);
            assertEquals(ghost.getTheta(), theta[i]);
            i++;
        }
        assertEquals(i, 2);
        i=1;
        ghost.nextTick();
        assertEquals(ghost.getX(), x[i]);
        assertEquals(ghost.getY(), y[i]);
        assertEquals(ghost.getTheta(), theta[i]);
    }
}
