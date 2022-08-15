package com.bank.parameter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ParameterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParameterApplication.class, args);
    }

}
