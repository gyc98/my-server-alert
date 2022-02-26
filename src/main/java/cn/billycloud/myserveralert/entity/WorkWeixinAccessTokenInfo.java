package cn.billycloud.myserveralert.entity;

import java.util.Date;

//保存token信息
public class WorkWeixinAccessTokenInfo {
    private String accessToken;
    private Date expireTime;

    public WorkWeixinAccessTokenInfo() {

    }

    public WorkWeixinAccessTokenInfo(String accessToken, Date expireTime) {
        this.accessToken = accessToken;
        this.expireTime = expireTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
