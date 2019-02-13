package cn.wimetro.service;

import cn.wimetro.service.unionpay.UnionPayConService;

/**
 *
 * @author wangwei
 * @date  2019-02-02
 **/
public interface SignService{

    /**
     * 银联签到交易
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    void doSign();

    /**
     * 读取配置文件
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    void doInit();
}
