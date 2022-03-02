package cn.billycloud.myserveralert.service.workweixin;

import cn.billycloud.myserveralert.dao.mapper.UserTokenMapper;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.redis.UserTokenRedisCache;
import cn.billycloud.myserveralert.service.UserPushSettingService;
import cn.billycloud.myserveralert.service.http.GetHelper;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class WorkWeixinAccessTokenService {
    @Autowired
    private UserTokenMapper userTokenMapper;
    @Autowired
    private UserPushSettingService userPushSettingService;
    @Autowired
    private UserTokenRedisCache userTokenRedisCache;

    private final String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    //可能返回null
    public WorkWeixinAccessTokenInfo getAccessToken(long userID){
        //先查询缓存
        Result result = userTokenRedisCache.getWorkWeixinAccessTokenInfo(userID);
        WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo = null;
        if(ResultCode.INTERFACE_EXCEED_LOAD.code() == result.getCode()){
            //访问禁止
            log.info("请求" + userID + "的企业微信token访问过高，暂时不可用");
            return null;
        }else if(ResultCode.SUCCESS.code() == result.getCode()){
            workWeixinAccessTokenInfo = (WorkWeixinAccessTokenInfo)result.getData();
        }
        //缓存里面没有
        if(workWeixinAccessTokenInfo == null){
            //应该标记一个临时空值 避免连续向数据库或远端获取
            userTokenRedisCache.setWorkWeixinAccessTokenInfo(userID, null);
            //缓存没有 就查询数据库
            workWeixinAccessTokenInfo = userTokenMapper.selectWorkWeixinToken(userID);
        }
        //从数据库或缓存中获取到的对象
        if(workWeixinAccessTokenInfo != null && workWeixinAccessTokenInfo.getExpireTime().after(new Date())){
            //加入缓存
            userTokenRedisCache.setWorkWeixinAccessTokenInfo(userID, workWeixinAccessTokenInfo);
            return workWeixinAccessTokenInfo;
        }
        //没有或已经过期
        //向远程获取
        result = userPushSettingService.getUserPushSettingInfo(userID);
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
            //保存进缓存
            userTokenRedisCache.setWorkWeixinAccessTokenInfo(userID, workWeixinAccessTokenInfo);
        }
        return workWeixinAccessTokenInfo;
    }

    //强制刷新accessToken
    public boolean forceFlushAccessToken(long userID){
        log.info("清空user的access_token：" + userID);
        //先清数据库
        int res = userTokenMapper.deleteWorkWeixinToken(userID);
        if(res == 0){
            log.info("未删除数据库中的accesstoken，可能没有");
        }
        //先清除缓存
        userTokenRedisCache.setWorkWeixinAccessTokenInfo(userID, null);

        //向远程获取
        WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo = null;
        Result result = userPushSettingService.getUserPushSettingInfo(userID);
        if((int)ResultCode.SUCCESS.code() == result.getCode()){
            UserPushSettingInfo userPushSettingInfo = (UserPushSettingInfo)result.getData();
            if(userPushSettingInfo.isWorkWeixinFilled()){
                workWeixinAccessTokenInfo = requestAccessToken(userPushSettingInfo.getWorkWeixinCorpID(), userPushSettingInfo.getWorkWeixinCorpSecret());
            }
        }
        if(workWeixinAccessTokenInfo != null){
            //保存进数据库
            res = userTokenMapper.setWorkWeixinToken(userID, workWeixinAccessTokenInfo);
            if(res == 0){
                log.error("保存企业微信accesstoken出错");
            }
            //保存进缓存
            userTokenRedisCache.setWorkWeixinAccessTokenInfo(userID, workWeixinAccessTokenInfo);
            return true;
        }
        return false;
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
