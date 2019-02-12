package cn.wimetro.start;

import cn.wimetro.remotecall.ConnManager;
import cn.wimetro.remotecall.YlNode;
import cn.wimetro.service.SignService;
import cn.wimetro.unit.LogFormatUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Order(value = 1)
public class UnionpayStart implements ApplicationRunner {

    @Value("${unionpay-server.unionpay.localNap.ip}")
    String localNap;
    @Value("${unionpay-server.unionpay.remote.ip}")
    String remoteIp;
    @Value("${unionpay-server.unionpay.remote.port}")
    String remotePort;

    @Autowired
    SignService signService;

    @Override
    public void run(ApplicationArguments args) {
        //初始化
        try {
            String localIp = localNap;
            List<YlNode> nodeList = new ArrayList<>();
            YlNode node = new YlNode();

            node.setIp(remoteIp);
            node.setPort(Integer.parseInt(remotePort));
            nodeList.add(node);
            ConnManager.getInstance().start(localIp,nodeList);

            signService.doSign();
            signService.doInit();
            LogFormatUnit.setLevelOne("银联签到完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
