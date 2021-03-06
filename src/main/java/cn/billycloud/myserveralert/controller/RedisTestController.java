package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.entity.WorkWeixinAccessTokenInfo;
import cn.billycloud.myserveralert.util.Result;
import cn.billycloud.myserveralert.util.ResultCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/redis")
public class RedisTestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/set")
    @ResponseBody
    public String setRedis(@RequestParam("key") String key, @RequestParam("val") String val){
        redisTemplate.opsForValue().set(key, val);
        return "finished";
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public String getRedis(@RequestParam("key") String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test(){
        redisTemplate.opsForValue().set("test_key", "", 10, TimeUnit.SECONDS);
        String str = (String) redisTemplate.opsForValue().get("test_key");
        System.out.println(str.isEmpty());
        System.out.println(str);
        return str;
    }
}
