package cn.billycloud.myserveralert.redis;

import cn.billycloud.myserveralert.dao.mapper.UserPushSettingMapper;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
public class UserPushSettingRedisCache {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserPushSettingMapper userPushSettingMapper;

    @Value("${cache.time.minutes}")
    private int cacheTimeMinutes;

    //将数据库和缓存写在一起
    public Result getUserPushSettingInfo(long userID){
        UserPushSettingInfo userPushSettingInfo = null;
        //尝试从缓存中获取
        try {
            String json = (String) redisTemplate.opsForValue().get("UserPushSettingInfo_" + userID);
            if(json != null){
                if(json.isEmpty()){
                    //之前缓存了空值 避免缓存穿透
                    return Result.failure(ResultCode.INTERFACE_EXCEED_LOAD);//临时不允许访问
                }else{
                    userPushSettingInfo = JSON.parseObject(json, UserPushSettingInfo.class);//获取缓存中的对象
                }
            }
            if(userPushSettingInfo != null){
                log.info("从缓存中获取用户push setting信息");
                return Result.success(ResultCode.SUCCESS, userPushSettingInfo);//将缓存中的对象返回
            }
        }catch (Exception e){
            log.error("读取缓存失败", e);
        }

        //缓存中没有 查询数据库
        userPushSettingInfo = null;
        try {
            userPushSettingInfo = userPushSettingMapper.selectSetting(userID);
        }catch (Exception e){
            log.error("向数据库查询push setting异常", e);
        }

        //加入到数据库中 如果为null保存空值 避免缓存穿透
        try {
            if(userPushSettingInfo == null){
                redisTemplate.opsForValue().set("UserPushSettingInfo_" + userID, "", 10, TimeUnit.SECONDS);
                return Result.failure(ResultCode.RESULE_DATA_NONE);
            }else{
                String json = JSON.toJSON(userPushSettingInfo).toString();
                redisTemplate.opsForValue().set("UserPushSettingInfo_" + userID, json, cacheTimeMinutes, TimeUnit.MINUTES);
                return Result.success(ResultCode.SUCCESS, userPushSettingInfo);//将对象返回
            }
        }catch (Exception e){
            log.error("向缓存中保存push setting异常", e);
            return Result.failure(ResultCode.FAILURE);
        }

    }

    //写入数据库
    public Result setUserPushSettingInfo(UserPushSettingInfo userPushSettingInfo){
        long userID = userPushSettingInfo.getUserID();
        //先将缓存中的值清空
        try {
            redisTemplate.opsForValue().set("UserPushSettingInfo_" + userID, "", 10, TimeUnit.SECONDS);
            String checkStr = (String) redisTemplate.opsForValue().get("UserPushSettingInfo_" + userID);
            if(checkStr == null || !checkStr.isEmpty()){
                //缓存中没有值或不为空
                return Result.failure(ResultCode.FAILURE, "缓存检查失败");
            }
        }catch (Exception e){
            log.error("清空缓存中的push setting信息出错", e);
            return Result.failure(ResultCode.FAILURE, "缓存清除失败");
        }

        //先保存到数据库
        int res = 0;
        try {
            res = userPushSettingMapper.addSetting(userPushSettingInfo);
        }catch (Exception e){
            log.error("向数据库中保存push setting信息出错", e);
            return Result.failure(ResultCode.FAILURE, "写入数据库失败");
        }

        try {
            if(res > 0){
                //保存到缓存中
                String json = JSON.toJSON(userPushSettingInfo).toString();
                redisTemplate.opsForValue().set("UserPushSettingInfo_" + userID, json, cacheTimeMinutes, TimeUnit.MINUTES);
                String checkStr = (String) redisTemplate.opsForValue().get("UserPushSettingInfo_" + userID);
                if(checkStr != null && checkStr.equals(json)){
                    //缓存写入成功
                    return Result.success(ResultCode.SUCCESS, "写入成功");
                }else {
                    return Result.success(ResultCode.FAILURE, "数据库写入成功，缓存更新失败");//缓存写入失败
                }
            }else{
                return Result.failure(ResultCode.FAILURE, "保存到数据库失败");
            }
        }catch (Exception e){
            log.error("向缓存中保存push setting信息出错", e);
            return Result.failure(ResultCode.FAILURE, "写入缓存失败");
        }


    }
}
