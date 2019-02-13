package cn.wimetro.service;

import cn.wimetro.service.unionpay.UnionPayConService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "start-unionpay-server")
public interface TestFeignService extends UnionPayConService {
}
