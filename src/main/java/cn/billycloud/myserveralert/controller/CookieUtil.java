package cn.billycloud.myserveralert.controller;

import cn.billycloud.myserveralert.entity.CookieInfo;
import cn.billycloud.myserveralert.entity.UserInfo;
import cn.billycloud.myserveralert.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class CookieUtil implements ApplicationContextAware {

    private static UserService userService;

    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        userService = applicationContext.getBean(UserService.class);
    }

    //从一个request中检查cookie是否有效
    public static UserInfo checkCookie(HttpServletRequest request){
        try {
            CookieInfo cookieInfo = new CookieInfo();
            int cnt = 0;
            for(Cookie cookie : request.getCookies()){
                switch (cookie.getName()){
                    case "msa_userName":
                        cookieInfo.setUserName(cookie.getValue());
                        cnt++;
                        break;
                    case "msa_userID":
                        cookieInfo.setUserID(Long.valueOf(cookie.getValue()));
                        cnt++;
                        break;
                    case "msa_cookieVal":
                        cookieInfo.setCookieVal(cookie.getValue());
                        cnt++;
                        break;
                }
            }
            if(cnt < 3){
                //没有获取到
                return null;
            }

            //开始判断
            UserInfo userInfo = userService.checkCookie(cookieInfo);
            return userInfo;

        }catch (Exception e){
            log.info("未获取到cookie", e);
            return null;
        }


    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
