package group06.desktop_racing_game;

import controller.Controller;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import model.Model;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.RaceInfo;

/**
 *a 2D racing game where you can choose to race tracks that are stored in an online database
 * you can also view the top time for every track and you can even choose to challenge ghosts.
 * Upon finishing your time is stored online and you can choose to save your ghost data too.
 * 
 * Also see our web application where you can create your own tracks.
 *
 */
public class App extends JFrame {
    private Controller controller;
    private Model model;
    private ArrayList<IResizeOberver> observers;
    
    
    public void setPanel(JPanel panel){
        setContentPane(panel);
        validate();
        panel.requestFocus();
    }
    
    public void reconfigure() {
        pack();
        setLocationRelativeTo(null);
    }
    
    public void maximize() {
        setExtendedState(Frame.MAXIMIZED_BOTH);
    }
    
    App() throws Exception {
        super("Racing 2D");

        JFrame.setDefaultLookAndFeelDecorated(true);
        model = new Model();
        controller = new Controller(this,model);
        observers = new ArrayList<IResizeOberver>();
        addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent ce) {
                for(int i=0;i<observers.size();i++){
                    observers.get(i).update(getWidth(),getHeight());
                }
            }

            public void componentMoved(ComponentEvent ce) {
            }

            public void componentShown(ComponentEvent ce) {
            }

            public void componentHidden(ComponentEvent ce) {
                //pauzeer?
            }
        });

        //controller.start(new RaceInfo(55, "00RECHTS", ""));
        //controller.showRaceOverview();
        controller.showLogin();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
    }

    public void addResizeObserver(IResizeOberver observer) {
        observers.add(observer);
    }

}
