/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group06.desktop_racing_game;

import controller.Controller;
import exceptions.LoginException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.Model;

/**
 * interface with user:
 * panel where the user should log in before he can play the game
 * the user is informed by the errorMessage if the login failed and why.
 * whether login succeeds or not is checked in controller
 * 
 * 
 * @author Lando
 */
public class LoginPanel extends JPanel {

    private JTextField userInput, passwordInput;
    private JLabel errorMessage;
    private Controller controller;
    private Model model;
    private JFrame frame;

    public LoginPanel(Controller controller, Model model, JFrame frame) {
        this.controller =controller;
        this.model = model;
        this.frame = frame;
        setLayout(null);
        
        JLabel userLabel = new JLabel("User");
        userLabel.setBounds(10, 10, 80, 25);
        add(userLabel);

        userInput = new JTextField(20);
        userInput.setBounds(100, 10, 160, 25);
        add(userInput);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 40, 80, 25);
        add(passwordLabel);

        passwordInput = new JPasswordField(20);
        passwordInput.setBounds(100, 40, 160, 25);
        add(passwordInput);

        makeLoginButton();
        makeErrorMessage();
        
        /*
         JButton registerButton = new JButton("register");
         registerButton.setBounds(180, 80, 80, 25);
         add(registerButton);
         */
    }
    private void makeErrorMessage(){
        errorMessage = new JLabel();
        errorMessage.setForeground(Color.red);
        errorMessage.setBounds(100, 80, 8000, 25);
        add(errorMessage);
    }
    private void makeLoginButton() {
        JButton loginButton = new JButton("Log in");
        frame.getRootPane().setDefaultButton(loginButton);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    controller.login(userInput.getText(), passwordInput.getText());
                }catch(LoginException lex){
                    errorMessage.setText(lex.getMessage());
                }
            }
        });
        loginButton.setBounds(10, 80, 80, 25);
        add(loginButton);
    }
}
