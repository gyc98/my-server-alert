package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.service.UserService;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    @ResponseBody
    public Result register(@RequestBody JSONObject jsonObject){
        String userName = jsonObject.getString("userName");
        String passwordSet = jsonObject.getString("passwordSet");
        log.info("收到注册请求 用户名：" + userName + " 密码：" + passwordSet);
        Result result = userService.addNewUser(userName, passwordSet);
        return result;
    }

    @RequestMapping("/login")
    @ResponseBody
    public Result login(@RequestBody JSONObject jsonObject){
        String userID = jsonObject.getString("userID");
        String passwordInput = jsonObject.getString("passwordInput");
        log.info("收到登录请求 用户账号：" + userID + " 密码：" + passwordInput);
        Result result = userService.userLogin(userID, passwordInput);
        return result;
    }
}
