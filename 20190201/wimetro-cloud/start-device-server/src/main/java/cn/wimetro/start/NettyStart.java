package cn.wimetro.start;

import cn.wimetro.netty.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(value = 1)
public class NettyStart implements ApplicationRunner {
    @Autowired
    NettyServer nettyServer;
    @Override
    public void run(ApplicationArguments args){
        nettyServer.run();
    }
}
