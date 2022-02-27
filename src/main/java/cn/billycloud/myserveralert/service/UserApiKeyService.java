package cn.billycloud.myserveralert.service;

import cn.billycloud.myserveralert.dao.mapper.UserMapper;
import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.redis.UserApiKeyRedisCache;
import cn.billycloud.myserveralert.util.HashHelper;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserApiKeyService {
    @Autowired
    private UserApiKeyRedisCache userApiKeyRedisCache;

    //刷新一个用户的apikey
    public Result flushApiKey(UserInfo userInfo){
        try {
            String raw = userInfo.getUserName() + userInfo.getUserID() + userInfo.getRegistrationTime() + userInfo.getLastLoginTime() + new Date().toString();
            String apiKey = HashHelper.md5(raw);
            //保存进去
            return userApiKeyRedisCache.setApiKey(userInfo.getUserID(), apiKey);
        }catch (Exception e){
            log.error("生成apikey失败", e);
            return Result.failure(ResultCode.FAILURE);
        }
    }

    //获取一个用户的apikey
    public Result getApiKey(UserInfo userInfo){
        return userApiKeyRedisCache.getApiKeyByUserID(userInfo.getUserID());
    }

    //通过apikey查询用户信息
    public Long getUserInfoByApiKey(String apiKey){
        Result result = userApiKeyRedisCache.getUserIDByApiKey(apiKey);
        if(ResultCode.SUCCESS.code() == result.getCode()){
            long userID = (long)result.getData();
            return userID;
        }
        return null;
    }
}
