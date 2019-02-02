package cn.wimetro.service.impl;

import cn.wimetro.constants.Ylstatic;
import cn.wimetro.entity.YlOptionConfig;
import cn.wimetro.mapper.YlOptionConfigMapper;
import cn.wimetro.pos.ISOF;
import cn.wimetro.service.YlOptionConfigService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangwei
 * @since 2019-01-08
 */
@Slf4j
@Service
public class YlOptionConfigServiceImpl extends ServiceImpl<YlOptionConfigMapper, YlOptionConfig> implements YlOptionConfigService {

    @Override
    public Map load8583Config() {
        Map map = new LinkedHashMap();
        List<YlOptionConfig> list = this.list(null);
        for(YlOptionConfig config : list){
            if(("DEV_PRO").equals(config.getCodeCode())){
                Ylstatic.DEV_PRO = config.getCodeValue();
                map.put("DEV_PRO",config.getCodeValue());
            }
            if(("gMACK").equals(config.getCodeCode())){
                ISOF.gMACK = ISOF.HexStr_Bytes(config.getCodeValue(),0);
                map.put("gMACK",config.getCodeValue());
            }
            if(("gMACK_bak").equals(config.getCodeCode())){
                ISOF.gMACK_bak = ISOF.HexStr_Bytes(config.getCodeValue(),0);
                map.put("gMACK_bak",config.getCodeValue());
            }
            if(("gDateTime").equals(config.getCodeCode())){
                ISOF.gDateTime = config.getCodeValue();
                map.put("gDateTime",config.getCodeValue());
            }
            if(("gHSMIP").equals(config.getCodeCode())){
                ISOF.gHSMIP = config.getCodeValue();
                map.put("gHSMIP",config.getCodeValue());
            }
            if(("gHSMPORT").equals(config.getCodeCode())){
                ISOF.gHSMPORT = config.getCodeValue();
                map.put("gHSMPORT",config.getCodeValue());
            }
            if(("gTPDU").equals(config.getCodeCode())){
                ISOF.gTPDU = config.getCodeValue();
                map.put("gTPDU",config.getCodeValue());
            }
            if(("gHEAD").equals(config.getCodeCode())){
                ISOF.gHEAD = config.getCodeValue();
                map.put("gHEAD",config.getCodeValue());
            }
            if(("gTerminal_no").equals(config.getCodeCode())){
                ISOF.gTerminal_no = config.getCodeValue();
                map.put("gTerminal_no",config.getCodeValue());
            }
            if(("gMerct_no").equals(config.getCodeCode())){
                ISOF.gMerct_no = config.getCodeValue();
                map.put("gMerct_no",config.getCodeValue());
            }
            if(("gSys_no").equals(config.getCodeCode())){
                ISOF.gSys_no = config.getCodeValue();
                map.put("gSys_no",config.getCodeValue());
            }
            if(("gMoney_code").equals(config.getCodeCode())){
                ISOF.gMoney_code = config.getCodeValue();
                map.put("gMoney_code",config.getCodeValue());
            }

            if(("PRE_AUTH_OVER_TIME").equals(config.getCodeCode())){
                Ylstatic.PRE_AUTH_OVER_TIME = config.getCodeValue();
                map.put("PRE_AUTH_OVER_TIME",config.getCodeValue());
            }

            if(("PRE_AUTH_DEAL_TIME").equals(config.getCodeCode())){
                Ylstatic.PRE_AUTH_DEAL_TIME = config.getCodeValue();
                map.put("PRE_AUTH_DEAL_TIME",config.getCodeValue());
            }

            if(("HIGH_PRICE").equals(config.getCodeCode())){
                Ylstatic.HIGH_PRICE = config.getCodeValue();
                map.put("HIGH_PRICE",config.getCodeValue());
            }
        }
        return map;
    }

    @Override
    public  void print8583Config(Map map) {
        map.forEach((k, v) -> {
            log.info(k+"===="+v);
        });
    }

    @Override
    public void update8583Config(Map map){
        String gMACK = (String) map.get("gMACK");
        String gMACKBak = (String) map.get("gMACK_bak");
        String gDateTime = (String) map.get("gDateTime");

        update("gMACK",gMACK);
        update("gMACK_bak",gMACKBak);
        update("gDateTime",gDateTime);
    }


    private void update(String name,String value){
        QueryWrapper<YlOptionConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CODE_GROUP","8583_LOGIN");
        queryWrapper.eq("CODE_CODE",name);

        YlOptionConfig ylOptionConfig =  this.getOne(queryWrapper);
        ylOptionConfig.setCodeValue(value);
        ylOptionConfig.updateById();
    }

}
