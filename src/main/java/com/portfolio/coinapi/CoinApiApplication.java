package com.portfolio.coinapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableCaching
@EnableTransactionManagement
public class CoinApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinApiApplication.class, args);
    }


}
