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
public class LoginResult {
    private final String _errorMessage;
    private final UserInfo _userInfo;
        
    public static LoginResult Failed(String errorMessage){
        if(errorMessage == null)
            throw new IllegalArgumentException("errorMessage");
        
        return new LoginResult(null, errorMessage);
    }
    
    public static LoginResult LoggedInAs(UserInfo userInfo){
        if(userInfo == null)
            throw new IllegalArgumentException("userInfo");
        
        return new LoginResult(userInfo, null);
    }
    
    private LoginResult(UserInfo _userInfo, String _errorMessage) {
        this._userInfo = _userInfo;
        this._errorMessage = _errorMessage;
    }
    
    public boolean isLoggedIn(){
        return _errorMessage == null;
    }
    
    public String getErrorMessage(){
        return _errorMessage;
    }
    
    public UserInfo getUserInfo(){
        return _userInfo;
    }
}
