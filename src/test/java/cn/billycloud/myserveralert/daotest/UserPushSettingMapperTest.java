package cn.billycloud.myserveralert.daotest;

import cn.billycloud.myserveralert.dao.mapper.UserPushSettingMapper;
import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserPushSettingMapperTest {
    @Autowired
    private UserPushSettingMapper userPushSettingMapper;

    @Test
    public void insertTest(){
        UserPushSettingInfo userPushSettingInfo = new UserPushSettingInfo(10, "a", "b", "v", "c");
        int res = userPushSettingMapper.addSetting(userPushSettingInfo);
        System.out.println(res);
    }
}
