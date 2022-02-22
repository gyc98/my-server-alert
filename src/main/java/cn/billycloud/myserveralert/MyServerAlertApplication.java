package cn.billycloud.myserveralert;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@MapperScan("cn.billycloud.myserveralert.dao.mapper")
public class MyServerAlertApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyServerAlertApplication.class, args);

		log.info("hello world!");
	}

}
