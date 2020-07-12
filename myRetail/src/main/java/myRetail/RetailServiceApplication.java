package myRetail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication(scanBasePackages = {"com.common", "com.services", "com.construct"}, exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@EnableMongoRepositories(basePackages = {"com.common.repository"})
@EntityScan("com.common.entity")
@EnableScheduling
@EnableWebSecurity
public class RetailServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RetailServiceApplication.class, args);
    }
}
