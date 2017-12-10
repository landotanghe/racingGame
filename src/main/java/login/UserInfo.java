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
    private String _userName;

    public UserInfo(String _userName) {
        this._userName = _userName;
    }
    
    public String getUserName(){
        return _userName;
    }
}
