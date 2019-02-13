package cn.wimetro.controller;

import cn.wimetro.netty.NettyClient;
import cn.wimetro.service.TestFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    public static String txts;

    @Autowired
    TestFeignService testFeignService;

    @Autowired
    NettyClient nettyClient;

    @RequestMapping("/test")
    public String hello(String txt){
        //testFeignService.test1("王巍");
        if(txt.isEmpty()){
            txt = "王巍11111";
        }
        nettyClient.callNettyClient(txt);
        return testFeignService.test1(txts);
    }
}

