package cn.wimetro.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {


    @RequestMapping("/test")
    public String hello(){
        log.info("ssssssssssssss");
        return "ssssssssssssss";
    }
}

