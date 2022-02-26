package cn.billycloud.myserveralert.dao.mapper;

import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface UserPushSettingMapper {
    @Insert("INSERT into user_push_setting (user_id, workweixin_corpid, workweixin_corpsecret, workweixin_agentid, workweixin_touser) " +
            "VALUES (#{userID}, #{workWeixinCorpID}, #{workWeixinCorpSecret}, #{workWeixinAgentID}, #{workWeixinToUser}) ON DUPLICATE KEY UPDATE " +
            "workweixin_corpid = #{workWeixinCorpID}, workweixin_corpsecret = #{workWeixinCorpSecret}, " +
            "workweixin_agentid = #{workWeixinAgentID}, workweixin_touser = #{workWeixinToUser}")
    int addSetting(UserPushSettingInfo userPushSettingInfo);

    @Select("SELECT * FROM user_push_setting where user_id = #{userID}")
    @Results({
            @Result(property = "userID",  column = "user_id"),
            @Result(property = "workWeixinCorpID",  column = "workweixin_corpid"),
            @Result(property = "workWeixinCorpSecret",  column = "workweixin_corpsecret"),
            @Result(property = "workWeixinAgentID",  column = "workweixin_agentid"),
            @Result(property = "workWeixinToUser",  column = "workweixin_touser"),
    })
    UserPushSettingInfo selectSetting(long userID);
}
