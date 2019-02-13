package cn.wimetro.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 银联系统公共接口
 * @author wangwei
 * @date  2019-02-12
 * @param
 * @return
 **/
public interface UnionPayConService {
    /**
     * 测试方法1
     * @author wangwei
     * @date  2019-02-12
     * @return
     **/
    @RequestMapping(value = "/unionpay/test1", method = RequestMethod.GET)
    String test1(@RequestParam("name") String name);
}
