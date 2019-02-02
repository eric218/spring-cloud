package cn.wimetro.service;

import cn.wimetro.entity.YlOptionConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangwei
 * @since 2019-01-08
 */
public interface YlOptionConfigService extends IService<YlOptionConfig> {

    /**
     * 读取8583配置文件
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    public Map load8583Config();

    public void print8583Config(Map map);

    /**
     * 银联签到
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    public void update8583Config(Map map);
}
