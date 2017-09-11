package com.example.ye.gametrade_in;

import android.app.Application;
import android.util.Base64;

import com.example.ye.gametrade_in.Bean.UserAuthenticationBean;
import com.example.ye.gametrade_in.Bean.UserBean;

/**
 * Created by ye on 2017/7/7.
 */

public class GameTradeInApplication extends Application {
    public String appVersion = "v1.0";

    public String serverUrl = "http://192.168.1.110:8080";
    // NOTE: commented out Ye's path
    // public String  serverUrl = "http://192.168.1.27:8080";

    public int limit = 4;

    private UserBean loginUser = new UserBean();
    private UserAuthenticationBean userAuthenticationBean = new UserAuthenticationBean();

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public UserBean GetLoginUser(){
        return loginUser;
    }

    public void SetUserLogin(UserBean user){
        loginUser.setUserId(user.getUserId());
    }

    public void SetUserLogout(){
        loginUser = new UserBean();
    }

    public UserAuthenticationBean GetUserAuthenticationBean() {
        return userAuthenticationBean;
    }

    public void SetUserAuthenticationBean(UserAuthenticationBean userAuthentication) {
        userAuthenticationBean.setUsername(userAuthentication.getUsername());
        userAuthenticationBean.setPassword(userAuthentication.getPassword());
    }

    public void SetUserAuthenticationOut(){
        userAuthenticationBean = new UserAuthenticationBean();
    }

    public String GetAuthorizedHeader(UserAuthenticationBean userAuthenticationBean){
        String credentials = userAuthenticationBean.getUsername() + ":" + userAuthenticationBean.getPassword();
        String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
        credBase64 = "Basic " + credBase64;
        return credBase64;
    }

}
