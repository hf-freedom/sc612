package com.aftersale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AfterSaleApplication {
    public static void main(String[] args) {
        SpringApplication.run(AfterSaleApplication.class, args);
    }
}
