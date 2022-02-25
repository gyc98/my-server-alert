package cn.billycloud.myserveralert.entity;

import java.util.Date;

//用户信息 javabean
public class UserInfo {
    private long userID = -1;//缺省值
    private String userName, passwordHash, salt;
    private Date registrationTime, lastLoginTime;
    private String cookie;

    public UserInfo(){

    }

    public UserInfo(String userName, String salt, String passwordHash, Date registrationTime, Date lastLoginTime) {
        this.userName = userName;
        this.salt = salt;
        this.passwordHash = passwordHash;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "userName: " + userName + "  userID: " + userID;
    }
}
