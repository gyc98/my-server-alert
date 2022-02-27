package cn.billycloud.myserveralert.redis;

import cn.billycloud.myserveralert.dao.mapper.UserMapper;
import cn.billycloud.myserveralert.dao.mapper.UserPushSettingMapper;
import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.entity.UserPushSettingInfo;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserCookieRedisCache {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //调用之前先确保userinfo中的上次登录时间和cookie信息改变了
    public Result setCookie(UserInfo userInfo){
        String cookie = userInfo.getCookie();
        String cacheKey = "cookie_" + cookie;
        //先将缓存中的值清空
        redisTemplate.opsForValue().set(cacheKey, "", 10, TimeUnit.SECONDS);
        String checkStr = (String) redisTemplate.opsForValue().get(cacheKey);
        if(checkStr == null || !checkStr.isEmpty()){
            //缓存中没有值或不为空
            return Result.failure(ResultCode.FAILURE, "缓存检查失败");
        }

        //先保存到数据库
        int res = 0;
        try {
            res = userMapper.updateLastLoginTimeAndCookie(userInfo.getUserID(), userInfo.getLastLoginTime(), cookie);
        }catch (Exception e){
            log.error("向数据库中保存cookie失败", e);
        }

        if(res > 0){
            try {
                //保存到缓存中
                String json = JSON.toJSON(userInfo).toString();
                redisTemplate.opsForValue().set(cacheKey, json, 1, TimeUnit.HOURS);
                checkStr = (String) redisTemplate.opsForValue().get(cacheKey);
                if(checkStr != null && checkStr.equals(json)){
                    //缓存写入成功
                    return Result.success(ResultCode.SUCCESS, "写入成功");
                }else {
                    return Result.success(ResultCode.FAILURE, "数据库写入成功，缓存更新失败");//缓存写入失败
                }
            }catch (Exception e){
                log.error("向缓存中保存cookie失败", e);
                return Result.success(ResultCode.FAILURE, "数据库写入成功，缓存更新失败");//缓存写入失败
            }

        }else{
            return Result.failure(ResultCode.FAILURE, "保存到数据库失败");
        }
    }

    public Result getCookie(String cookie){
        //先在缓存中查找
        String cacheKey = "cookie_" + cookie;
        try {
            String cacheVal = (String)redisTemplate.opsForValue().get(cacheKey);
            if(cacheVal != null){
                if(cacheVal.isEmpty()){
                    //空值 暂时无法请求
                    //之前缓存了空值 避免缓存穿透
                    return Result.failure(ResultCode.INTERFACE_EXCEED_LOAD);//临时不允许访问
                }else{
                    //解析
                    UserInfo userInfo = JSON.parseObject(cacheVal, UserInfo.class);//获取缓存中的对象
                    if(userInfo != null){
                        log.info("从缓存中获取用户cookie信息");
                        return Result.success(ResultCode.SUCCESS, userInfo);
                    }
                }
            }
        }catch (Exception e){
            log.error("缓存获取cookie异常", e);
        }

        //向数据库中获取
        UserInfo userInfo = null;
        try {
            userInfo = userMapper.selectByCookie(cookie);
        }catch (Exception e){
            log.error("向数据库中查询cookie失败");
            return Result.success(ResultCode.FAILURE);
        }

        try {
            if(userInfo != null){
                //保存到缓存中
                String json = JSON.toJSON(userInfo).toString();
                redisTemplate.opsForValue().set(cacheKey, json, 1, TimeUnit.HOURS);
                return Result.success(ResultCode.SUCCESS, userInfo);
            }else{
                redisTemplate.opsForValue().set(cacheKey, "", 10, TimeUnit.SECONDS);
                return Result.success(ResultCode.RESULE_DATA_NONE);
            }
        }catch (Exception e){
            log.error("向缓存中保存cookie信息失败", e);
            return Result.success(ResultCode.FAILURE);
        }
    }


}
