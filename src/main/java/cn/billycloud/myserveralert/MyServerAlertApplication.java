package cn.billycloud.myserveralert;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
@MapperScan("cn.billycloud.myserveralert.dao.mapper")
public class MyServerAlertApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(MyServerAlertApplication.class, args);
		log.info("hello world!");
	}

}
