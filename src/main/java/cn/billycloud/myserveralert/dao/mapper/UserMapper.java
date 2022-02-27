package cn.billycloud.myserveralert.dao.mapper;

import cn.billycloud.myserveralert.entity.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface UserMapper {

    @Insert("INSERT into user_info (user_name, salt, password_hash, registration_time, last_login_time) " +
            "VALUES(#{userName}, #{salt}, #{passwordHash}, #{registrationTime}, #{lastLoginTime})")
    @Options(useGeneratedKeys = true, keyProperty = "userID", keyColumn = "user_id")
    int insert(UserInfo userInfo);//用户编号自动生成 不创建 返回用户编号

    @Select("SELECT * FROM user_info")
    @Results({
            @Result(property = "userID",  column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "salt", column = "salt"),
            @Result(property = "passwordHash", column = "password_hash"),
            @Result(property = "registrationTime", column = "registration_time"),
            @Result(property = "lastLoginTime", column = "last_login_time"),
            @Result(property = "cookie", column = "cookie"),
    })
    List<UserInfo> selectAll();

    @Select("SELECT * FROM user_info WHERE user_id = #{userID}")
    @Results({
            @Result(property = "userID",  column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "salt", column = "salt"),
            @Result(property = "passwordHash", column = "password_hash"),
            @Result(property = "registrationTime", column = "registration_time"),
            @Result(property = "lastLoginTime", column = "last_login_time"),
            @Result(property = "cookie", column = "cookie"),
    })
    UserInfo selectByUserID(long userID);

    @Select("SELECT * FROM user_info WHERE cookie = #{cookie}")
    @Results({
            @Result(property = "userID",  column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "salt", column = "salt"),
            @Result(property = "passwordHash", column = "password_hash"),
            @Result(property = "registrationTime", column = "registration_time"),
            @Result(property = "lastLoginTime", column = "last_login_time"),
            @Result(property = "cookie", column = "cookie"),
    })
    UserInfo selectByCookie(String cookie);

//    @Update("UPDATE user_info SET last_login_time = #{lastLoginTime} WHERE user_id = #{userID}")
//    int updateLastLoginTime(@Param("userID") long userID, @Param("lastLoginTime") Date lastLoginTime);
//
//    @Update("UPDATE user_info SET cookie = #{cookie} WHERE user_id = #{userID}")
//    int updateCookie(@Param("userID") long userID, @Param("cookie") String cookie);

    @Update("UPDATE user_info SET last_login_time = #{lastLoginTime}, cookie = #{cookie} WHERE user_id = #{userID}")
    int updateLastLoginTimeAndCookie(@Param("userID") long userID, @Param("lastLoginTime") Date lastLoginTime, @Param("cookie") String cookie);


}
