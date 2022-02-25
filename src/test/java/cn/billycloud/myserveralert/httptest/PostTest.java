package cn.billycloud.myserveralert.httptest;

import cn.billycloud.myserveralert.service.http.GetHelper;
import cn.billycloud.myserveralert.service.http.PostHelper;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class PostTest {
    @Test
    public void test() throws Exception {
        GetHelper.send("https://www.baidu.com", new HashMap<>());
    }
}
