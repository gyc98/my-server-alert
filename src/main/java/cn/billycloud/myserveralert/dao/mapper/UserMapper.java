package cn.billycloud.myserveralert.dao.mapper;

import cn.billycloud.myserveralert.entity.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface UserMapper {

    @Insert("INSERT into user_info (user_name, password, registration_time, last_login_time) " +
            "VALUES(#{userName}, #{password}, #{registrationTime}, #{lastLoginTime})")
    @Options(useGeneratedKeys = true, keyProperty = "userID", keyColumn = "user_id")
    int insert(UserInfo userInfo);//用户编号自动生成 不创建 返回用户编号

    @Select("SELECT * FROM user_info")
    @Results({
            @Result(property = "userID",  column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "password", column = "password"),
            @Result(property = "registrationTime", column = "registration_time"),
            @Result(property = "lastLoginTime", column = "last_login_time"),
    })
    List<UserInfo> selectAll();


}
