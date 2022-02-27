package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.service.UserApiKeyService;
import cn.billycloud.myserveralert.service.workweixin.WorkWeixinMessagePushService;
import cn.billycloud.myserveralert.service.workweixin.WorkWeixinService;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/message")
public class MessagePushController {

    @Autowired
    private UserApiKeyService userApiKeyService;
    @Autowired
    WorkWeixinMessagePushService workWeixinMessagePushService;

    @RequestMapping(value = "/push")
    @ResponseBody
    public Result getPushSetting(@RequestBody JSONObject jsonParam){
        String apiKey = jsonParam.getString("api_key");
        String message = jsonParam.getString("message");
        if(apiKey == null || apiKey.isEmpty()){
            return Result.failure(ResultCode.DATA_IS_WRONG, "请输入api_key");
        }
        if(message == null || message.isEmpty()){
            return Result.failure(ResultCode.DATA_IS_WRONG, "请输入msg");
        }
        //检查api_key
        Long userID = userApiKeyService.getUserInfoByApiKey(apiKey);
        if(userID == null){
            return Result.failure(ResultCode.DATA_IS_WRONG, "api_key无效");
        }
        return workWeixinMessagePushService.push(userID, message);
    }
}
