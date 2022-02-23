package cn.billycloud.myserveralert.servicetest;

import cn.billycloud.myserveralert.service.UserService;
import cn.billycloud.myserveralert.util.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void addUserTest(){
        Result result = userService.addNewUser("testUser", "12345678");
        System.out.println(result);
        String userID = (String)result.getData();

        //登录
        result = userService.userLogin(userID, "12345678");
        System.out.println(result);
        if(Result.success().getCode() == result.getCode()){
            System.out.println("登录成功");
            System.out.println(result.getData());
        }

    }
}
