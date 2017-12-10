/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import model.Model;

/**
 *
 * @author lando
 */
public class ConnectionFactory {
    String _jdbc_url;
    String _jdbc_login;
    String _jdbc_password;
    
    public ConnectionFactory() throws Exception{
        ResourceBundle resources = ResourceBundle.getBundle("databankconstanten");
        
        _jdbc_url = resources.getString("connectiestring");
        _jdbc_login = resources.getString("username");
        _jdbc_password = resources.getString("password");
    }
    
    public Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(_jdbc_url, _jdbc_login, _jdbc_password);
        return con;
    }
}
