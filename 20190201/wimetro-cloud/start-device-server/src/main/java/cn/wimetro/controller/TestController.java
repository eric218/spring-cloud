package cn.wimetro.controller;

import cn.wimetro.netty.NettyClient;
import cn.wimetro.service.TestFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@Slf4j
@RestController
public class TestController {

    @Autowired
    AsyncTasks asyncTasks;

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

    @RequestMapping("/task1")
    public String task1() throws Exception {
        long start = System.currentTimeMillis();
        String ss = testFeignService.test1("wanf");
        long end = System.currentTimeMillis();
        String result = ss+"  任务全部完成，总耗时：" + (end - start) + "毫秒";
        return result;
    }

    /**
     * 测试异步任务。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/task")
    public String task() throws Exception {
        long start = System.currentTimeMillis();

        Future<String> task1 = asyncTasks.doTaskOne();
        Future<String> task2 = asyncTasks.doTaskTwo();
        Future<String> task3 = asyncTasks.doTaskThree();

//        while(true) {
//            if(task1.isDone() && task2.isDone() && task3.isDone()) {
//                // 三个任务都调用完成，退出循环等待
//                break;
//            }
//            Thread.sleep(1000);
//        }

        long end = System.currentTimeMillis();

        String result = "任务全部完成，总耗时：" + (end - start) + "毫秒";
        return result;
    }
}

