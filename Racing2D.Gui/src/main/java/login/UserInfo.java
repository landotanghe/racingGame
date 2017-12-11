/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

/**
 *
 * @author lando
 */
public class UserInfo {

    private final int _userId;
    private final String _userName;

    public UserInfo(int userId, String userName) {
        _userId = userId;
        _userName = userName;
    }
    
    public int getId(){
        return _userId;
    }
    
    public String getUserName(){
        return _userName;
    }
}
