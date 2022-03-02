package cn.billycloud.myserveralert.rabbit;

import cn.billycloud.myserveralert.service.UserApiKeyService;
import cn.billycloud.myserveralert.service.workweixin.WorkWeixinMessagePushService;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RabbitListener(queues = "${my.rabbitmq.queue.name}")
@Slf4j
public class RabbitReceiver {

    @Autowired
    private UserApiKeyService userApiKeyService;
    @Autowired
    WorkWeixinMessagePushService workWeixinMessagePushService;

    @RabbitHandler
    public void process(String context) {
//        System.out.println("Receiver  : " + context);
        //处理推送
        JSONObject jsonParam = JSON.parseObject(context);
        String apiKey = jsonParam.getString("api_key");
        String message = jsonParam.getString("message");

        if(apiKey == null || apiKey.isEmpty()){
            log.error("没有api_key");
            return;
//            return Result.failure(ResultCode.DATA_IS_WRONG, "请输入api_key");
        }
        if(message == null || message.isEmpty()){
            log.error("没有消息");
            return;
//            return Result.failure(ResultCode.DATA_IS_WRONG, "请输入msg");
        }
        //检查api_key
        Long userID = userApiKeyService.getUserInfoByApiKey(apiKey);
        if(userID == null){
            log.error("无法通过api_key获取到用户: " + apiKey);
            return;
//            return Result.failure(ResultCode.DATA_IS_WRONG, "api_key无效");
        }
        try {
            Result result = workWeixinMessagePushService.push(userID, message, true);
            if((int)ResultCode.SUCCESS.code() != result.getCode()){
                log.error("推送失败：" + result);
            }else{
                log.info("成功向用户：" + userID + "  推送：" + message);
            }
        }catch (Exception e){
            log.error("推送异常", e);
        }

    }
}
