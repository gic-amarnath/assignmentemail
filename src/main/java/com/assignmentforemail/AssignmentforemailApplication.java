package com.assignmentforemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories
public class AssignmentforemailApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentforemailApplication.class, args);
		System.out.println("============email=============");
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
