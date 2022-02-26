package cn.billycloud.myserveralert.service.workweixin;

import cn.billycloud.myserveralert.dao.mapper.UserTokenMapper;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.service.UserPushSettingService;
import cn.billycloud.myserveralert.service.http.GetHelper;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WorkWeixinAccessTokenService {
    @Autowired
    private UserTokenMapper userTokenMapper;
    @Autowired
    private UserPushSettingService userPushSettingService;

    private final String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    //可能返回null
    public WorkWeixinAccessTokenInfo getAccessToken(long userID){
        //先查询数据库
        WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo = userTokenMapper.selectWorkWeixinToken(userID);
        if(workWeixinAccessTokenInfo != null && workWeixinAccessTokenInfo.getExpireTime().after(new Date())){
            return workWeixinAccessTokenInfo;
        }
        //获取新的
        Result result = userPushSettingService.getUserPushSettingInfo(userID);
        if(ResultCode.SUCCESS.code() == result.getCode()){
            UserPushSettingInfo userPushSettingInfo = (UserPushSettingInfo)result.getData();
            if(userPushSettingInfo.isWorkWeixinFilled()){
                workWeixinAccessTokenInfo = requestAccessToken(userPushSettingInfo.getWorkWeixinCorpID(), userPushSettingInfo.getWorkWeixinCorpSecret());
            }
        }
        if(workWeixinAccessTokenInfo != null){
            //保存进数据库
            int res = userTokenMapper.setWorkWeixinToken(userID, workWeixinAccessTokenInfo);
            if(res == 0){
                log.error("保存企业微信accesstoken出错");
            }
        }
        return workWeixinAccessTokenInfo;
    }

    //请求accessToken
    private WorkWeixinAccessTokenInfo requestAccessToken(String corpid, String corpsecret){
        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("corpid", corpid);
            requestMap.put("corpsecret", corpsecret);
            JSONObject jsonObject = GetHelper.send(url, requestMap);
            int errCode = jsonObject.getInteger("errcode");
            if(errCode == 0){
                String accessToken = jsonObject.getString("access_token");
                int expiresIn = jsonObject.getInteger("expires_in");
                System.out.println(accessToken);
                System.out.println(expiresIn);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.SECOND, expiresIn);
                return new WorkWeixinAccessTokenInfo(accessToken, calendar.getTime());
            }else{
                String errMsg = jsonObject.getString("errmsg");
                log.error("获取accessToken异常：" + errCode + "  " + errMsg);
            }

        }catch (Exception e){
            log.error("获取accessToken异常", e);
        }
        return null;
    }
}
