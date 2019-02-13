package cn.wimetro.service;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "start-unionpay-server")
public interface TestFeignService extends UnionPayConService {
}
