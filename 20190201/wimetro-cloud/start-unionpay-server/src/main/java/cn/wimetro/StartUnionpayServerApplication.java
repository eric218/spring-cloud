package cn.wimetro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class StartUnionpayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartUnionpayServerApplication.class, args);
    }

}

