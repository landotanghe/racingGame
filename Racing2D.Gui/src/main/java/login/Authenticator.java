/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import exceptions.LoginException;

/**
 *
 * @author lando
 */
public interface Authenticator {

    LoginResult login(String login, String password) throws LoginException;
    
}
