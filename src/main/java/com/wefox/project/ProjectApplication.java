package com.wefox.project;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ProjectApplication {

	@Autowired
	private AccountRepository accountRepository;

	final String paymentUrl = "http://localhost:9000/payment";
	final String logUrl = "http://localhost:9000/log";
	PaymentService payment;

	public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

	@EnableKafka
	@Configuration
	public class KafkaConsumerConfig {
		@Bean
		public ConsumerFactory<String, String> consumerFactory() {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
			props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			return new DefaultKafkaConsumerFactory<>(props);
		}
		@Bean
		public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
			ConcurrentKafkaListenerContainerFactory<String, String>
					factory = new ConcurrentKafkaListenerContainerFactory<>();
			factory.setConsumerFactory(consumerFactory());
			return factory;
		}
	}

	@KafkaListener(topics = {"online" , "offline"}, groupId = "group-id")
	public void listen(String message) {

		// Step 1: Populate Database
		payment = new PaymentService();
		payment.populate(message);

		try {
			accountRepository.save(payment);
		}catch (JSONException err){
			logError("Error saving Payment", err.toString());
		}

		// Step 2: Post the log data
		RestService paymentApi = new RestService(paymentUrl,payment.getPayload());

		// check response
		if (paymentApi.getResponse().getStatusCode() == HttpStatus.OK) {
			System.out.println("Payment Request Successful");
		} else {
			System.out.println("Payment Request Failed");
			String code = paymentApi.getResponse().getStatusCode().toString();
			logError("Payment Request Failed", "Status code" + code);
		}

	}

	public void logError(String error, String description) {

		Map<String,Object> logPayload = new HashMap<>();
		logPayload.put("payment_id", payment.getPayment_id());
		logPayload.put("error_type", error);
		logPayload.put("error_description", description);

		// Step 2: Post the log data
		RestService logApi = new RestService(logUrl,logPayload);

		// check response
		if (logApi.getResponse().getStatusCode() == HttpStatus.OK) {
			System.out.println("Log Request Successful");
		} else {
			System.out.println("Log Request Failed");
		}
	}
}
