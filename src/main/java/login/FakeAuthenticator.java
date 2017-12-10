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
public class FakeAuthenticator implements Authenticator{

    public LoginResult login(String login, String password) throws LoginException {
        return LoginResult.LoggedInAs(new UserInfo(login));
    }    
}
