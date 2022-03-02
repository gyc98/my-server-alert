package cn.billycloud.myserveralert.daotest;

import cn.billycloud.myserveralert.dao.mapper.UserApiKeyMapper;
import cn.billycloud.myserveralert.redis.UserApiKeyRedisCache;
import cn.billycloud.myserveralert.util.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserApiKeyMapperTest {
    @Autowired
    private UserApiKeyRedisCache userApiKeyRedisCache;

//    @Test
    public void test(){
        Result result = userApiKeyRedisCache.setApiKey(10, "111");
        System.out.println(result.getMsg());
    }
}
