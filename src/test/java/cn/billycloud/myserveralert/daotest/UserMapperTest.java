package cn.billycloud.myserveralert.daotest;

import cn.billycloud.myserveralert.dao.mapper.UserMapper;
import cn.billycloud.myserveralert.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void insertTest(){
        UserInfo userInfo = new UserInfo("test_user", "123", new Date(), new Date());
        int res = userMapper.insert(userInfo);
        if(res > 0){
            System.out.println("user_id: " + userInfo.getUserID());
        }

    }

    @Test
    public void selectTest(){
        List<UserInfo> list = userMapper.selectAll();
        for(UserInfo userInfo : list){
            System.out.println(userInfo);
        }
    }

}
