package cn.billycloud.myserveralert.workweixintest;

import cn.billycloud.myserveralert.service.workweixin.AccessTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccessTokenTest {
    @Autowired
    private AccessTokenService accessTokenService;

    @Test
    public void test(){
        accessTokenService.requestAccessToken();
    }
}
