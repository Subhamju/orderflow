package com.orderflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderflowOmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderflowOmsApplication.class, args);
	}

}
