package cn.billycloud.myserveralert.entity;

import java.util.Date;

//用户信息 javabean
public class UserInfo {
    private long userID = -1;//缺省值
    private String userName, password;
    private Date registrationTime, lastLoginTime;

    public UserInfo(){

    }

    public UserInfo(String userName, String password, Date registrationTime, Date lastLoginTime) {
        this.userName = userName;
        this.password = password;
        this.registrationTime = registrationTime;
        this.lastLoginTime = lastLoginTime;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "userName: " + userName + "  userID: " + userID;
    }
}
