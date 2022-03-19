package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.service.UserPushSettingService;
import cn.billycloud.myserveralert.util.NeedCookieCheck;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/pushsetting")
public class PushSettingController {

    @Autowired
    private UserPushSettingService userPushSettingService;

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    @NeedCookieCheck
    public Result getPushSetting(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        log.info("session_id: " + session.getId());
        UserInfo userInfo = CookieAspect.userInfoThreadLocal.get();
//        UserInfo userInfo = CookieUtil.checkCookie(request);
        if(userInfo == null){
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);//用户未登录
        }
        Result result = userPushSettingService.getUserPushSettingInfo(userInfo.getUserID());
        return result;
    }

    @RequestMapping(value = "/set", method = RequestMethod.POST)
    @ResponseBody
    @NeedCookieCheck
    public Result setPushSetting(HttpServletRequest request, HttpServletResponse response, @RequestBody UserPushSettingInfo userPushSettingInfo){
        UserInfo userInfo = CookieAspect.userInfoThreadLocal.get();
//        UserInfo userInfo = CookieUtil.checkCookie(request);
        if(userInfo == null){
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);//用户未登录
        }
        if(userPushSettingInfo == null){
            return Result.failure(ResultCode.DATA_IS_WRONG);
        }
        userPushSettingInfo.setUserID(userInfo.getUserID());
        //更新
        Result result = userPushSettingService.setUserPushSettingInfo(userPushSettingInfo);
        return result;
    }
}
