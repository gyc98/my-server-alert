package cn.billycloud.myserveralert.redis;

import cn.billycloud.myserveralert.dao.mapper.UserApiKeyMapper;
import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
public class UserApiKeyRedisCache {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserApiKeyMapper userApiKeyMapper;

    public Result getUserIDByApiKey(String apiKey){
        //现在缓存中查找
        String cacheKey = "apikey_" + apiKey;
        try {
            String cacheVal = (String) redisTemplate.opsForValue().get(cacheKey);
            if(cacheVal != null){
                if(cacheVal.isEmpty()){
                    //空值 暂时无法处理
                    return Result.failure(ResultCode.INTERFACE_EXCEED_LOAD);//临时不允许访问
                }else{
                    long userID = Long.valueOf(cacheVal);
                    log.info("从缓存中获取api_key对应user_id信息");
                    return Result.success(ResultCode.SUCCESS, userID);
                }
            }
        }catch (Exception e){
            log.error("读取缓存失败", e);
        }

        //向数据库中获取
        Long userID = userApiKeyMapper.selectUserID(apiKey);
        if(userID == null){
            //一段时间不处理
            redisTemplate.opsForValue().set(cacheKey, "", 10, TimeUnit.SECONDS);
            return Result.failure(ResultCode.RESULE_DATA_NONE);
        }else{
            //先保存
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(userID), 1, TimeUnit.HOURS);
            return Result.success(ResultCode.SUCCESS, userID);//将对象返回
        }
    }

    public Result setApiKey(long userID, String apiKey){
        String cacheKey = "apikey_" + apiKey;
        //先将缓存中的值清空
        redisTemplate.opsForValue().set(cacheKey, "", 10, TimeUnit.SECONDS);
        String checkStr = (String) redisTemplate.opsForValue().get(cacheKey);
        if(checkStr == null || !checkStr.isEmpty()){
            //缓存中没有值或不为空
            return Result.failure(ResultCode.FAILURE, "缓存检查失败");
        }

        //先保存到数据库
        //先update
        int res = 0;
        try {
            res = userApiKeyMapper.updateApiKey(userID, apiKey);
            if(res == 0){
                res = userApiKeyMapper.insertApiKey(userID, apiKey);
            }
        }catch (Exception e){
            log.error("向数据库中保存apikey失败", e);
        }

        if(res > 0){
            try {
                //保存到缓存中
                redisTemplate.opsForValue().set(cacheKey, String.valueOf(userID), 1, TimeUnit.HOURS);
                checkStr = (String) redisTemplate.opsForValue().get(cacheKey);
                if(checkStr != null && checkStr.equals(String.valueOf(userID))){
                    //缓存写入成功
                    return Result.success(ResultCode.SUCCESS, "写入成功");
                }else {
                    return Result.success(ResultCode.FAILURE, "数据库写入成功，缓存更新失败");//缓存写入失败
                }
            }catch (Exception e){
                log.error("向缓存中保存apikey失败", e);
                return Result.success(ResultCode.FAILURE, "数据库写入成功，缓存更新失败");//缓存写入失败
            }
        }else{
            return Result.failure(ResultCode.FAILURE, "保存到数据库失败");
        }
    }

    //没有缓存 直接获取
    public Result getApiKeyByUserID(long userID){
        try {
            String apiKey = userApiKeyMapper.selectApiKey(userID);
            if(apiKey != null){
                return Result.success(ResultCode.SUCCESS, apiKey);
            }else {
                return Result.failure(ResultCode.FAILURE);
            }
        }catch (Exception e){
            log.error("获取apikey异常", e);
            return Result.failure(ResultCode.FAILURE);
        }
    }
}
