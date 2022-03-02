package cn.billycloud.myserveralert.rabbittest;

import cn.billycloud.myserveralert.rabbit.RabbitSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@SpringBootTest
public class RabbitTest {

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void test() throws InterruptedException {
        rabbitSender.send("123");
        Thread.sleep(5000);
    }
}
