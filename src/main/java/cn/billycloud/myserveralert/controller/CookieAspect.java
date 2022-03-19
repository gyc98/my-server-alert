package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.util.NoLoginExpection;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@Aspect
public class CookieAspect {

    public static ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();

    @Pointcut("@annotation(cn.billycloud.myserveralert.util.NeedCookieCheck)")
    public void pointcut(){

    }

    @Before("pointcut()")         //point-cut expression
    public void before(JoinPoint joinPoint) throws NoLoginExpection {
        userInfoThreadLocal.set(null);
        //获取request
        HttpServletRequest request = null;

        for(Object arg : joinPoint.getArgs()){
            if(arg instanceof HttpServletRequest){
                request = (HttpServletRequest)arg;
                break;
            }
        }

        if(request == null){
            log.info("无法获取到request");
//            throw new NoLoginExpection();
            return;
        }

        UserInfo userInfo = CookieUtil.checkCookie(request);

        if(userInfo == null){
            log.info("当前用户未登录");
            return;
        }

        userInfoThreadLocal.set(userInfo);


//        System.out.println("before() : " + joinPoint.getTarget());
    }
}
