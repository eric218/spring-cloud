package cn.wimetro.service;

import cn.wimetro.entity.YlPreAuthReq;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangwei
 * @since 2019-01-08
 */
public interface YlPreAuthReqService extends IService<YlPreAuthReq> {
    /**
     * 获取序列值
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    int selectLine01Seq();

}
