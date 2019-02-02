package cn.wimetro.service;


public interface SignService {

    /**
     * 银联签到交易
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    public void doSign();

    /**
     * 读取配置文件
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    public void doInit();
}
