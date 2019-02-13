package cn.wimetro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
/**
 *
 * @author wangwei
 * @date  2019-02-13
 * @param
 * @return
 **/
@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication
public class StartUnionPayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartUnionPayServerApplication.class, args);
    }

}

