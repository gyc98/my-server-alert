package cn.billycloud.myserveralert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class MyServerAlertApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyServerAlertApplication.class, args);

		log.info("hello world!");
	}

}
