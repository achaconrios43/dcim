package com.example.clases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.clases.dao")
public class ClasesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClasesApplication.class, args);
    }

}
