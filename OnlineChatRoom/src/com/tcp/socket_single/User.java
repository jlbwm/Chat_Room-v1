package com.tcp.socket_single;

/**
 * Created by Jiaxin Li on 4/30/19.
 */
public class User {
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    private String userID;
    private String pwd;

    public User(String userID, String pwd) {
        this.userID = userID;
        this.pwd = pwd;
    }
}
