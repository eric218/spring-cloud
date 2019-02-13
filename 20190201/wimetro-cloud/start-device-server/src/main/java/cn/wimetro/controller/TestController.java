package cn.wimetro.controller;

import cn.wimetro.service.TestFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @Autowired
    TestFeignService testFeignService;

    @RequestMapping("/test")
    public String hello(){
        //testFeignService.test1("王巍");
        return testFeignService.test1("王巍");
    }
}

