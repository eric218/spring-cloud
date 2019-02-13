package cn.wimetro.controller;

import cn.wimetro.service.TestFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Future;
@Component
@Slf4j
public class AsyncTasks {

    @Autowired
    TestFeignService testFeignService;

    public static Random random = new Random();

    @Async
    public Future<String> doTaskOne() throws Exception {
        System.out.println("Async, taskOne, Start...");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        log.info(testFeignService.test1("test1"));
        System.out.println("Async, taskOne, End, 耗时: " + (end - start) + "毫秒");
        return new AsyncResult<>("AsyncTaskOne Finished");
    }

    @Async
    public Future<String> doTaskTwo() throws Exception {
        System.out.println("Async, taskTwo, Start");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        System.out.println("Async, taskTwo, End, 耗时: " + (end - start) + "毫秒");
        return new AsyncResult<>("AsyncTaskTwo Finished");
    }

    @Async
    public Future<String> doTaskThree() throws Exception {
        System.out.println("Async, taskThree, Start");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("Async, taskThree, End, 耗时: " + (end - start) + "毫秒");
        return new AsyncResult<>("AsyncTaskThree Finished");
    }
}
