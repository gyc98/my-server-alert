package cn.billycloud.myserveralert.entity;

public class UserPushSettingInfo {
    private long userID;
    private String workWeixinCorpID, workWeixinCorpSecret, workWeixinAgentID, workWeixinToUser;

    public UserPushSettingInfo(long userID, String workWeixinCorpID, String workWeixinCorpSecret, String workWeixinAgentID, String workWeixinToUser) {
        this.userID = userID;
        this.workWeixinCorpID = workWeixinCorpID;
        this.workWeixinCorpSecret = workWeixinCorpSecret;
        this.workWeixinAgentID = workWeixinAgentID;
        this.workWeixinToUser = workWeixinToUser;
    }

    public UserPushSettingInfo() {

    }

    public boolean isWorkWeixinFilled(){
        if(workWeixinCorpID == null || workWeixinCorpID.isEmpty()){
            return false;
        }
        if(workWeixinCorpSecret == null || workWeixinCorpSecret.isEmpty()){
            return false;
        }
        if(workWeixinAgentID == null || workWeixinAgentID.isEmpty()){
            return false;
        }
        if(workWeixinToUser == null || workWeixinToUser.isEmpty()){
            return false;
        }
        return true;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getWorkWeixinCorpID() {
        return workWeixinCorpID;
    }

    public void setWorkWeixinCorpID(String workWeixinCorpID) {
        this.workWeixinCorpID = workWeixinCorpID;
    }

    public String getWorkWeixinCorpSecret() {
        return workWeixinCorpSecret;
    }

    public void setWorkWeixinCorpSecret(String workWeixinCorpSecret) {
        this.workWeixinCorpSecret = workWeixinCorpSecret;
    }

    public String getWorkWeixinAgentID() {
        return workWeixinAgentID;
    }

    public void setWorkWeixinAgentID(String workWeixinAgentID) {
        this.workWeixinAgentID = workWeixinAgentID;
    }

    public String getWorkWeixinToUser() {
        return workWeixinToUser;
    }

    public void setWorkWeixinToUser(String workWeixinToUser) {
        this.workWeixinToUser = workWeixinToUser;
    }
}
