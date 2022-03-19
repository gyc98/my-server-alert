package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.util.NeedCookieCheck;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping("/aspecttest")
public class AspectTestController {
    @RequestMapping(value = "/test")
    @ResponseBody
    @NeedCookieCheck
    public Result test(HttpServletRequest request, HttpServletResponse response){
        UserInfo userInfo = CookieAspect.userInfoThreadLocal.get();
        log.info("test");
        return Result.success();
    }
}
