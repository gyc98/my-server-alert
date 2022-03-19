package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.service.UserApiKeyService;
import cn.billycloud.myserveralert.util.NeedCookieCheck;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping("/apikey")
public class ApiKeyController {

    @Autowired
    private UserApiKeyService userApiKeyService;

    //处理apikey相关请求

    @RequestMapping(value = "/flush")
    @ResponseBody
    @NeedCookieCheck
    public Result flush(HttpServletRequest request, HttpServletResponse response){
        UserInfo userInfo = CookieAspect.userInfoThreadLocal.get();
//        //先检查身份
//        UserInfo userInfo = CookieUtil.checkCookie(request);
        if(userInfo == null){
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);//用户未登录
        }
        Result result = userApiKeyService.flushApiKey(userInfo);
        return result;
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    @NeedCookieCheck
    public Result get(HttpServletRequest request, HttpServletResponse response){
        UserInfo userInfo = CookieAspect.userInfoThreadLocal.get();
//        //先检查身份
//        UserInfo userInfo = CookieUtil.checkCookie(request);
        if(userInfo == null){
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);//用户未登录
        }
        Result result = userApiKeyService.getApiKey(userInfo);
        return result;
    }
}
