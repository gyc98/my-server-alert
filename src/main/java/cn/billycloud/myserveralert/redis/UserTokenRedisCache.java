package cn.billycloud.myserveralert.redis;

import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserTokenRedisCache {
    @Autowired
    private RedisTemplate redisTemplate;

    //缓存UserToken数据
    public void setWorkWeixinAccessTokenInfo(long userID, WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo){
        try {
            String json = "";//空
            if(workWeixinAccessTokenInfo != null){
                json = JSON.toJSON(workWeixinAccessTokenInfo).toString();
                redisTemplate.opsForValue().set("WorkWeixinAccessTokenInfo_" + userID, json, 1, TimeUnit.HOURS);
            }else{
                redisTemplate.opsForValue().set("WorkWeixinAccessTokenInfo_" + userID, json, 10, TimeUnit.SECONDS);
            }
        }catch (Exception e){
            log.error("写入缓存失败", e);
        }
    }

    //读取缓存
    public Result getWorkWeixinAccessTokenInfo(long userID){
        try {
            String json = (String) redisTemplate.opsForValue().get("WorkWeixinAccessTokenInfo_" + userID);
            if(json == null){
                //缓存没有
                return Result.failure(ResultCode.RESULE_DATA_NONE);
            }
            if(json.isEmpty()){
                //临时空值
                return Result.failure(ResultCode.INTERFACE_EXCEED_LOAD);//临时不允许访问
            }
            WorkWeixinAccessTokenInfo workWeixinAccessTokenInfo = JSON.parseObject(json, WorkWeixinAccessTokenInfo.class);
            return Result.success(ResultCode.SUCCESS, workWeixinAccessTokenInfo);
        }catch (Exception e){
            log.error("读取缓存失败", e);
        }
        return Result.failure(ResultCode.RESULE_DATA_NONE);
    }
}
