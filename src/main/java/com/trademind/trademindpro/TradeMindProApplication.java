package com.trademind.trademindpro;

import com.trademind.trademindpro.ratelimiter.service.RateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradeMindProApplication {

    public static void main(String[] args) {

        SpringApplication.run(TradeMindProApplication.class, args);

        RateLimiter rateLimiter = new RateLimiter();

        for (int i = 1; i <= 6; i++) {

            boolean allowed = rateLimiter.allowRequest("Nandini");

            if (allowed) {
                System.out.println("Request " + i + " : Allowed");
            } else {
                System.out.println("Request " + i + " : Rejected");
            }
        }
    }
}