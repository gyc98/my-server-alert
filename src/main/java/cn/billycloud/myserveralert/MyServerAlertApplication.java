package cn.billycloud.myserveralert;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@Slf4j
@MapperScan("cn.billycloud.myserveralert.dao.mapper")
public class MyServerAlertApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyServerAlertApplication.class, args);
		log.info("hello world!");
	}

}
