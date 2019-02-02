package cn.wimetro.mapper;

import cn.wimetro.entity.YlPreAuthReq;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangwei
 * @since 2019-01-08
 */
public interface YlPreAuthReqMapper extends BaseMapper<YlPreAuthReq> {
    /**
     * 获取序列值
     * @author wangwei
     * @date  2019/01/08
     * @param
     * @return
     **/
    int selectLine01Seq();
}
