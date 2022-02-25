package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.CookieInfo;
import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.service.UserService;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result register(@RequestParam("userName") String userName, @RequestParam("passwordSet") String passwordSet){
        log.info("收到注册请求 用户名：" + userName + " 密码：" + passwordSet);
        Result result = userService.addNewUser(userName, passwordSet);
        return result;
    }

    @RequestMapping("/login")
    @ResponseBody
    public Result login(HttpServletRequest request, HttpServletResponse response, @RequestParam("userID") String userID, @RequestParam("passwordInput") String passwordInput){
        log.info("收到登录请求 用户账号：" + userID + " 密码：" + passwordInput);
        Result result = userService.userLogin(userID, passwordInput);
        if(ResultCode.SUCCESS.code() == result.getCode()){
            //登录成功设置cookie
            CookieInfo cookieInfo = (CookieInfo)result.getData();
            addCookie(response, "msa_userName", cookieInfo.getUserName());
            addCookie(response, "msa_userID", String.valueOf(cookieInfo.getUserID()));
            addCookie(response, "msa_lastLoginTime", URLEncoder.encode(cookieInfo.getLastLoginTime().toString()));
            addCookie(response, "msa_cookieVal", cookieInfo.getCookieVal());
            result.setData("登录成功");
        }
        return result;
    }

    @RequestMapping("/cookie")
    @ResponseBody
    public Result cookieCheck(HttpServletRequest request, HttpServletResponse response){
        UserInfo userInfo = CookieUtil.checkCookie(request);
        if(userInfo == null){
            return Result.failure(ResultCode.DATA_IS_WRONG);
        }else{
            return Result.success(ResultCode.SUCCESS, userInfo.getUserName());
        }
    }

    private static void addCookie(HttpServletResponse response, String name, String val){
        Cookie cookie = new Cookie(name, val);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
