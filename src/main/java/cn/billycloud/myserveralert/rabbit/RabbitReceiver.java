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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(name="use_rabbit_receiver", havingValue="true")
@RabbitListener(queues = "${my.rabbitmq.queue.name}")
@Slf4j
public class RabbitReceiver {

    @Autowired
    private UserApiKeyService userApiKeyService;
    @Autowired
    WorkWeixinMessagePushService workWeixinMessagePushService;

    @RabbitHandler
    public void process(String context) {
        Long userID = null;
        String message = null;
        try {
            //处理推送
            JSONObject jsonParam = JSON.parseObject(context);
            String apiKey = jsonParam.getString("api_key");
            message = jsonParam.getString("message");

            if(apiKey == null || apiKey.isEmpty()){
                log.error("没有api_key");
                return;
            }
            if(message == null || message.isEmpty()){
                log.error("没有消息");
                return;
            }
            //检查api_key
            userID = userApiKeyService.getUserInfoByApiKey(apiKey);
            if(userID == null){
                log.error("无法通过api_key获取到用户: " + apiKey);
                return;
            }

        }catch (Exception e){
            log.error("处理消息队列中消息失败", e);
            return;
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
