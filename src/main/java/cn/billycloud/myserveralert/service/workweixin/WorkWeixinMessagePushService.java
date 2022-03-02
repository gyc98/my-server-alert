package cn.billycloud.myserveralert.service.workweixin;

import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.service.UserPushSettingService;
import cn.billycloud.myserveralert.service.base.MessagePushService;
import cn.billycloud.myserveralert.service.http.PostHelper;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkWeixinMessagePushService implements MessagePushService {

    @Autowired
    private WorkWeixinAccessTokenService accessTokenService;
    @Autowired
    private UserPushSettingService userPushSettingService;

    @Override
    public Result push(long userID, String message, boolean allowRetry) {
        //先获取企业微信的accesstoken
        WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo = accessTokenService.getAccessToken(userID);
        if(workWeixinAccessTokenInfo == null){
            return Result.failure(ResultCode.RESULE_DATA_NONE, "未获取到该用户的企业微信access token");
        }
        //获取其他参数
        Result result = userPushSettingService.getUserPushSettingInfo(userID);
        UserPushSettingInfo userPushSettingInfo = null;
        if(ResultCode.SUCCESS.code() == result.getCode()){
            //获取成功
            userPushSettingInfo = (UserPushSettingInfo)result.getData();
        }
        if(userPushSettingInfo == null){
            return Result.failure(ResultCode.RESULE_DATA_NONE, "无法获取用户的推送设置");
        }
        //发送请求
        result = sendMessage(workWeixinAccessTokenInfo.getAccessToken(), userPushSettingInfo.getWorkWeixinAgentID(), userPushSettingInfo.getWorkWeixinToUser(), message);
        if((int)ResultCode.RETRY.code() == result.getCode() && allowRetry){
            //access token失效 重新获取
            if(accessTokenService.forceFlushAccessToken(userID)){
                //重试一次
                result = push(userID, message, false);
            }else{
                log.info("无法重新获取企业微信access token 不能重试");
            }
        }
        return result;
    }

    private final String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    private Result sendMessage(String accessToken, String agentID, String toUser, String text){
        try {
            if(toUser == null || toUser.isEmpty()){
                toUser = "@all";//默认推送给所有人
            }
            //准备post请求
            Map<String, String> map = new HashMap<>();
            map.put("access_token", accessToken);
            //post请求参数
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("touser", toUser);
            requestMap.put("msgtype", "text");
            requestMap.put("agentid", agentID);
            Map<String, String> textMap = new HashMap<>();
            textMap.put("content", text);
            requestMap.put("text", textMap);

            JSONObject responseJson = PostHelper.send(url, map, requestMap);
            int errCode = responseJson.getInteger("errcode");
            String errMsg = responseJson.getString("errmsg");
            log.info("errCode: " + errCode + "  errMsg: " + errMsg);
            if(errCode == 0){
                return Result.success(ResultCode.SUCCESS, errMsg);
            }else if(errCode == 40014 || errCode == 42001){//token无效或过期
                //重试 是否重试由上层决定
                return Result.failure(ResultCode.RETRY);
            }else {
                return Result.success(ResultCode.FAILURE, errCode + ": " + errMsg);
            }
        }catch (Exception e){
            log.error("向企业微信推送信息失败", e);
            return Result.success(ResultCode.FAILURE, e.getMessage());
        }

    }
}
