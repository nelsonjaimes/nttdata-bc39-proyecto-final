package com.nttdata.bc39.grupo04.bootcoin;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.nttdata.bc39.grupo04"})
@EnableEurekaClient
public class BootcoinServiceApplication {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication.run(BootcoinServiceApplication.class, args);
    }

}