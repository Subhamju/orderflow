package com.orderflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.orderflow.kafka.OrderKafkaProducer;

@SpringBootTest
@ActiveProfiles("test")
class OrderflowOmsApplicationTests {

	@MockBean
	private OrderKafkaProducer orderKafkaProducer;

	@Test
	void contextLoads() {
	}

}
