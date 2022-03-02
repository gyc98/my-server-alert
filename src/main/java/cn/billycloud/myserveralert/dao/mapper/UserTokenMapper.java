package cn.billycloud.myserveralert.dao.mapper;

import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import org.apache.ibatis.annotations.*;

public interface UserTokenMapper {
    @Select("Select * FROM user_token where user_id = #{userID}")
    @Results({
            @Result(property = "accessToken", column = "workweixin_access_token"),
            @Result(property = "expireTime", column = "workweixin_access_expire_time")
    })
    WorkWeixinAccessTokenInfo selectWorkWeixinToken(@Param("userID") long userID);

    @Insert("INSERT into user_token (user_id, workweixin_access_token, workweixin_access_expire_time) " +
            "VALUES (#{userID}, #{token.accessToken}, #{token.expireTime}) ON DUPLICATE KEY UPDATE " +
            "workweixin_access_token = #{token.accessToken}, workweixin_access_expire_time = #{token.expireTime}")
    int setWorkWeixinToken(@Param("userID") long userID, @Param("token") WorkWeixinAccessTokenInfo token);

    @Delete("Delete From user_token where user_id = #{userID}")
    int deleteWorkWeixinToken(@Param("userID") long userID);
}
