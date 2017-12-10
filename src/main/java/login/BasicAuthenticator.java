/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import exceptions.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

/**
 *
 * @author lando
 */
public class BasicAuthenticator implements Authenticator {;
    
    @Override
    public LoginResult login(String login, String password) throws LoginException {
        final int MIN_LOGIN_LENGTH = 3,
                MAX_LOGIN_LENGTH = 16,
                MIN_PASS_LENGTH = 6,
                MAX_PASS_LENGTH = 16;
        
        if (login == null || login.length() == 0) {
            return LoginResult.Failed("login required");
        } else if (login.length() < MIN_LOGIN_LENGTH || login.length() > MAX_LOGIN_LENGTH) {
            return LoginResult.Failed("login must be " + MIN_LOGIN_LENGTH + " - " + MAX_LOGIN_LENGTH + "chars");
        } else if (password == null || password.length() == 0) {
            return LoginResult.Failed("password required");
        } else if (password.length() < MIN_PASS_LENGTH || password.length() > MAX_PASS_LENGTH) {
            return LoginResult.Failed("password must be " + MIN_LOGIN_LENGTH + " - " + MAX_LOGIN_LENGTH + " chars");
        }
        
        return LoginResult.LoggedInAs(new UserInfo(login));
    }
    
    private void authenticate(String login, String password){
        try {
            Connection con = getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(resources.getString("select_user"));
                stmt.setString(1, login);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String email = rs.getString(4);
                    int uid = rs.getInt(1);
                    user = new User(uid, login, email);
                } else {
                    throw new LoginException("wrong username or password");
                }

            } catch (SQLException e) {
                throw new LoginException("Could not connect to server");
            } finally {
                con.close();
            }
        } catch (SQLException e) {
            throw new LoginException("Could not connect to server");
        }
    }
}