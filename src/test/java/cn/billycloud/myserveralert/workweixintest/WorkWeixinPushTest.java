package cn.billycloud.myserveralert.workweixintest;

import cn.billycloud.myserveralert.service.workweixin.WorkWeixinMessagePushService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WorkWeixinPushTest {
    @Autowired
    private WorkWeixinMessagePushService workWeixinMessagePushService;

    @Test
    public void test(){
        for(int i = 0; i < 10; i++){
            workWeixinMessagePushService.push(22, "测试消息: " + i);
        }

    }
}
