/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import data.ConnectionFactory;
import exceptions.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author lando
 */
public class BasicAuthenticator implements Authenticator {
    private final ConnectionFactory _connectionFactory;

    public BasicAuthenticator(ConnectionFactory _connectionFactory) {
        this._connectionFactory = _connectionFactory;
    }   
        
    @Override
    public LoginResult login(String login, String password) throws LoginException {
        final int MIN_LOGIN_LENGTH = 3,
                MAX_LOGIN_LENGTH = 16,
                MIN_PASS_LENGTH = 3,
                MAX_PASS_LENGTH = 16;
        
        if (login == null || login.length() == 0) {
            return LoginResult.Failed("login required");
        } else if (login.length() < MIN_LOGIN_LENGTH || login.length() > MAX_LOGIN_LENGTH) {
            return LoginResult.Failed("login must be " + MIN_LOGIN_LENGTH + " - " + MAX_LOGIN_LENGTH + "chars");
        } else if (password == null || password.length() == 0) {
            return LoginResult.Failed("password required");
        } else if (password.length() < MIN_PASS_LENGTH || password.length() > MAX_PASS_LENGTH) {
            return LoginResult.Failed("password must be " + MIN_PASS_LENGTH + " - " + MAX_LOGIN_LENGTH + " chars");
        }
        UserInfo user = authenticate(login, password);
        
        if(user == null){
            return LoginResult.Failed("could not connect to login service");            
        }
        
        return LoginResult.LoggedInAs(user);
    }
    
    private UserInfo authenticate(String login, String password){
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM tbl_logins WHERE USERNAME = ? AND PASSWORD = ?");
                stmt.setString(1, login);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String email = rs.getString(4);
                    int uid = rs.getInt(1);
                    return new UserInfo(uid, login);
                } else {
                    return null;
                }

            } catch (SQLException e) {
                return null;
            } finally {
                con.close();
            }
        } catch (SQLException e) {
                return null;
        }
    }
}
