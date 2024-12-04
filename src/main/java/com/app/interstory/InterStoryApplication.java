package com.app.interstory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.app.interstory")
public class InterStoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterStoryApplication.class, args);
    }

}
