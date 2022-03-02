package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.rabbit.RabbitSender;
import cn.billycloud.myserveralert.service.UserApiKeyService;
import cn.billycloud.myserveralert.service.workweixin.WorkWeixinMessagePushService;
import cn.billycloud.myserveralert.service.workweixin.WorkWeixinService;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
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
    private RabbitSender sender;

    @RequestMapping(value = "/push")
    @ResponseBody
    public Result getPushSetting(@RequestBody String str){
        try {
            //加入到消息队列中
            sender.send(str);
            return Result.success(ResultCode.SUCCESS, "已加入到消息队列");
        }catch (Exception e){
            log.error("加入消息队列出错", e);
            return Result.failure(ResultCode.FAILURE, "运行异常");
        }
    }
}
