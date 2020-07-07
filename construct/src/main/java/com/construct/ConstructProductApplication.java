package com.construct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.common", "com.construct"})
@EnableMongoRepositories(basePackages = {"com.common.repository"})
@EntityScan("com.common.entity")
public class ConstructProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConstructProductApplication.class, args);
    }
}
