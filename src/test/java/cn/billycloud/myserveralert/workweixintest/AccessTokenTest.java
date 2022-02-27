package cn.billycloud.myserveralert.workweixintest;

import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.service.workweixin.WorkWeixinAccessTokenService;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class AccessTokenTest {
    @Autowired
    private WorkWeixinAccessTokenService accessTokenService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test(){
        WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo = accessTokenService.getAccessToken(22);
        redisTemplate.opsForValue().set("WorkWeixinAccessTokenInfo_" + 22, JSON.toJSON(workWeixinAccessTokenInfo).toString(), 1, TimeUnit.HOURS);
        System.out.println(workWeixinAccessTokenInfo.getAccessToken());
        System.out.println(workWeixinAccessTokenInfo.getExpireTime());
    }

    @Test
    public void testRedis(){
        WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo = JSON.parseObject((String) redisTemplate.opsForValue().get("WorkWeixinAccessTokenInfo_" + 22), WorkWeixinAccessTokenInfo.class);
        System.out.println(workWeixinAccessTokenInfo.getAccessToken());
        System.out.println(workWeixinAccessTokenInfo.getExpireTime());
    }
}
