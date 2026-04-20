package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner run() {
		return args -> {

			try {
				RestTemplate restTemplate = new RestTemplate();

				String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

				Map<String, String> body = new HashMap<>();
				body.put("name", "Kush Dastane");
				body.put("regNo", "ADT23SOCB0555");
				body.put("email", "kushdastane777@gmail.com");

				log.info("Sending request to generate webhook...");

				ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);

				String webhook = (String) response.getBody().get("webhook");
				String token = (String) response.getBody().get("accessToken");

				log.info("Webhook URL: {}", webhook);

				String finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";

				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", token);
				headers.setContentType(MediaType.APPLICATION_JSON);

				Map<String, String> requestBody = new HashMap<>();
				requestBody.put("finalQuery", finalQuery);

				HttpEntity<Map<String, String>> request =
						new HttpEntity<>(requestBody, headers);

				log.info("Submitting SQL solution...");

				restTemplate.postForEntity(webhook, request, String.class);

				log.info("Submission completed successfully!");

			} catch (Exception e) {
				log.error("Error occurred: {}", e.getMessage());
			}
		};
	}
}