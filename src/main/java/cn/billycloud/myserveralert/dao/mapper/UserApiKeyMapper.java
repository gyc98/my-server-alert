package cn.billycloud.myserveralert.dao.mapper;

import org.apache.ibatis.annotations.*;

public interface UserApiKeyMapper {

    @Select("SELECT api_key FROM user_api_key where user_id = #{userID}")
    String selectApiKey(long userID);

    @Select("SELECT user_id FROM user_api_key where api_key = #{apiKey}")
    long selectUserID(String apiKey);

    @Update("UPDATE user_api_key SET api_key = #{apiKey} where user_id = #{userID}")
    int updateApiKey(@Param("userID") long userID, @Param("apiKey") String apiKey);

    @Insert("INSERT into user_api_key (user_id, api_key) VALUES (#{userID}, #{apiKey})")
    int insertApiKey(@Param("userID") long userID, @Param("apiKey") String apiKey);
}
