package cn.billycloud.myserveralert.service;

import cn.billycloud.myserveralert.dao.mapper.UserPushSettingMapper;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.redis.UserPushSettingRedisCache;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserPushSettingService {

    @Autowired
    private UserPushSettingRedisCache userPushSettingRedisCache;

    public Result getUserPushSettingInfo(long userID){
        //获取 缓存+数据库
        Result result = userPushSettingRedisCache.getUserPushSettingInfo(userID);
        UserPushSettingInfo userPushSettingInfo = null;
        if(ResultCode.SUCCESS.code() == result.getCode()){
            userPushSettingInfo = (UserPushSettingInfo)result.getData();
        }

        if(userPushSettingInfo == null){
            //创建一个空的
            userPushSettingInfo = new UserPushSettingInfo();
            userPushSettingInfo.setUserID(userID);
            //先不插入数据库
        }
        return Result.success(ResultCode.SUCCESS, userPushSettingInfo);
    }

    public Result setUserPushSettingInfo(UserPushSettingInfo userPushSettingInfo){
        if(userPushSettingInfo == null){
            return Result.failure(ResultCode.DATA_IS_WRONG, "请输入有效信息");
        }
        //先将之前缓存中的设置为控制
        Result res = userPushSettingRedisCache.setUserPushSettingInfo(userPushSettingInfo);
        return res;
    }
}
