package cn.wimetro.service.impl;

import cn.wimetro.entity.YlPreAuthReq;
import cn.wimetro.mapper.YlPreAuthReqMapper;
import cn.wimetro.service.YlPreAuthReqService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangwei
 * @since 2019-01-08
 */
@Service
public class YlPreAuthReqServiceImpl extends ServiceImpl<YlPreAuthReqMapper, YlPreAuthReq> implements YlPreAuthReqService {
    @Autowired
    YlPreAuthReqMapper ylPreAuthReqMapper;
    @Override
    public int selectLine01Seq() {
        return ylPreAuthReqMapper.selectLine01Seq();
    }
}
