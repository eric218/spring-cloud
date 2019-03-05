package cn.wimetro.controller;

import cn.wimetro.service.UnionPayConService;
import cn.wimetro.service.YlOptionConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
    @Value("${server.port}")
    private String port ;
    @Autowired
    YlOptionConfigService ylOptionConfigService;
    @Override
    public String test1(@RequestParam("name") String name) {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        log.info(host + ": " + name);
        ylOptionConfigService.print8583Config(ylOptionConfigService.load8583Config());
        return host + ": " + name;
    }
}
