package cn.wimetro.service.match;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 匹配系统公共接口
 * @author wangwei
 * @date  2019-02-12
 * @param
 * @return
 **/
public interface MatchConService {
    /**
     * 测试方法1
     * @author wangwei
     * @date  2019-02-12
     * @return
     **/
    @RequestMapping(value = "/match/test1", method = RequestMethod.GET)
    String test1();
}
