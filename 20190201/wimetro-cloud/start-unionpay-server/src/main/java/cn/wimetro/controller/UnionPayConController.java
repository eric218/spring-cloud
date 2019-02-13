package cn.wimetro.controller;

import cn.wimetro.service.UnionPayConService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 银联对外服务接口实现
 * @author wangwei
 * @date  2019-02-13
 * @param
 * @return
 **/
@Slf4j
@RestController
public class UnionPayConController implements UnionPayConService {

    @Override
    public String test1(@RequestParam("name") String name) {
        log.info("UnionPayConController104:" + name);
        return "UnionPayConController.test2:" + name;
    }
}
