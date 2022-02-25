package cn.billycloud.myserveralert.entity;

import cn.billycloud.myserveralert.util.HashHelper;
import cn.billycloud.myserveralert.util.MyException;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

//cookie用于保存用户的登录信息
public class CookieInfo {
    private long userID;
    private String userName;
    private Date lastLoginTime;
    private String cookieVal;

    public static CookieInfo generate(UserInfo userInfo) throws NoSuchAlgorithmException, MyException {
        CookieInfo cookieInfo = new CookieInfo();
        cookieInfo.setUserID(userInfo.getUserID());
        cookieInfo.setUserName(userInfo.getUserName());
        cookieInfo.setLastLoginTime(userInfo.getLastLoginTime());
        //生成cookieVal
        cookieInfo.setCookieVal(convertUserInfo(userInfo));
        return cookieInfo;
    }

    private static String convertUserInfo(UserInfo userInfo) throws NoSuchAlgorithmException, MyException {
        String raw = userInfo.getUserName() + userInfo.getUserID() + userInfo.getLastLoginTime() + userInfo.getRegistrationTime() + new Date().getTime();
        String md5 = HashHelper.md5(raw);
        return md5;
    }

    public CookieInfo(){

    }

    public CookieInfo(long userID, String userName, Date lastLoginTime, String cookieVal) {
        this.userID = userID;
        this.userName = userName;
        this.lastLoginTime = lastLoginTime;
        this.cookieVal = cookieVal;
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

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getCookieVal() {
        return cookieVal;
    }

    public void setCookieVal(String cookieVal) {
        this.cookieVal = cookieVal;
    }
}
