package com.fiserv.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Payment Authorization Microservice Application
 *
 * Main entry point for the Spring Boot application
 */
@SpringBootApplication
@EnableKafka
@ComponentScan(basePackages = "com.fiserv.payment")
public class PaymentAuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentAuthorizationApplication.class, args);
    }
}

