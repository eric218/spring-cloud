package cn.wimetro.service.impl;

import cn.wimetro.constants.YlConstants;
import cn.wimetro.constants.Ylstatic;
import cn.wimetro.pos.ISOF;
import cn.wimetro.pos.PKGResult;
import cn.wimetro.pos.TradeLogin;
import cn.wimetro.pos.TradeResetKey;
import cn.wimetro.remotecall.ConnManager;
import cn.wimetro.remotecall.MessageVO;
import cn.wimetro.service.SignService;
import cn.wimetro.service.YlOptionConfigService;
import cn.wimetro.service.YlPreAuthReqService;
import cn.wimetro.unit.DateUtil;
import cn.wimetro.unit.LogFormatUnit;
import cn.wimetro.unit.NumberUtil;
import com.unionpay.common.communication.concurrentutil.asyntosyn.WaitResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


/**
 * 签到业务逻辑
 * @author wangwei
 * @date  2019/01/08
 * @param
 * @return
 **/
@Service
@Slf4j
public class SignServiceImpl implements SignService {

    @Autowired
    YlPreAuthReqService ylPreAuthReqService;

    @Autowired
    YlOptionConfigService ylOptionConfigService;

    @Override
    public void doSign() {
        log.info(LogFormatUnit.setLevelOne("银联签到开始"));

        //银联的标志位是0开始签到 必要条件
        Ylstatic.QIANDAO_FLAG="00";

        //读取配置文件
        Map configMap = ylOptionConfigService.load8583Config();
        ylOptionConfigService.print8583Config(configMap);

        log.info("-----银联签到交易开始-----");
        //接受返回值
        Map resultMap = new HashMap(16);
        PKGResult res;
        TradeLogin login = new TradeLogin();
        //交易日期时间, 10字节
        String binDate = DateUtil.getDateTimeNew();
        login.bIn_Date     = binDate.substring(4,binDate.length()).getBytes();
        int number = ylPreAuthReqService.selectLine01Seq();
        String tradeNo = NumberUtil.getTradeNo(number);
        //闸机的交易流水号, 6字节
        login.bIn_TradeNo  = tradeNo.getBytes();
        login.bIn_BatNo    = "000008".getBytes();
        res = login.seal();
        if( res.iResult < 0 )
        {
            log.info("组包发生错误:"+res.iResult);
            return;
        }
        MessageVO vo = new MessageVO();
        try{
            //发送数据
            String key = "QD001" + tradeNo;
            vo.setKey(key);
            vo.setMsgBody(res.bResult);

            //接收数据
            resultMap = sendAutPreMessage(vo);
            PKGResult resTemp = (PKGResult)resultMap.get("r1");
            res.bResult = resTemp.bResult;
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally {
            ConnManager.getInstance().getWaitSet().removeWaitResult(vo.getKey());
        }

        //解包
        res = login.unseal( res.bResult);
        if( res.iResult < 0 )
        {
            log.info("签到出错,解包完成出错:"+res.iResult);
            return;
        }

        if(YlConstants.YL_RETURN_00.equals(new String(login.getField(YlConstants.YL_FIELD_39))))
        {
            log.info("-----银联申请密钥重置返回00成功-----");
            vo = new MessageVO();
            try {
                PKGResult resTemp = (PKGResult)resultMap.get("r2");
                res.bResult = resTemp.bResult;

                //银行密钥重置
                PKGResult res0810 = resetKey(res.bResult);

                vo.setKey("QD003");
                vo.setMsgBody(res0810.bResult);

                //直接发送银行 不需要返回值
                ConnManager.getInstance().sendMsg(vo);

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                ConnManager.getInstance().getWaitSet().removeWaitResult(vo.getKey());
            }

            Map map = new HashMap(16);

            map.put("gMACK", ISOF.Bytes_HexStr(ISOF.gMACK));
            map.put("gMACK_bak", ISOF.Bytes_HexStr(ISOF.gMACK_bak));
            map.put("gDateTime", ISOF.gDateTime);
            ylOptionConfigService.update8583Config(map);
            log.info("-----密钥数据更新后的结果-----");
            Map cMap = ylOptionConfigService.load8583Config();
            ylOptionConfigService.print8583Config(cMap);
            log.info("-----密钥数据更新成功-----");

            log.info("-----响应银联密钥重置申请成功-----");
            log.info("!!!!!!!!!!-----新的银联请求方法结束----!!!!!!!!!");
        }else{
            log.info("-----银联签到交易失败-----");
        }

    }

    @Override
    public void doInit() {
        //设置ISOF的值
        log.info("开始读取 =========8583Config");
        Map map = ylOptionConfigService.load8583Config();
        ylOptionConfigService.print8583Config(map);
        log.info("读取完毕 =========8583Config");
    }


    public  static PKGResult resetKey(byte[] pkgData)
    {
        PKGResult res = null;
        byte[] retCode;
        TradeResetKey setKey = new TradeResetKey();

        try
        {
            //解包
            res = setKey.unseal( pkgData);

            retCode = (new String("00")).getBytes();
            if( res.iResult < 0 )
            {
                //闸机的交易流水号, 6字节
                setKey.bIn_retCode  = (new String("B0")).getBytes();
                log.info("解包完成:"+res.iResult);
                if(res.iResult == ISOF.ERR_MAC)
                {
                    retCode = (new String("A0")).getBytes();
                }
                else if(res.iResult == ISOF.ERR_CHECKVALUE)
                {
                    retCode = (new String("B0")).getBytes();
                }
                else
                {
                    retCode = (new String("00")).getBytes();
                    //retCode = (new String("B1")).getBytes()
                }
            }

            //getField里参数是N，取不到值改Y
            //输入时间MMDDhhmmss
            setKey.bIn_Date  	= setKey.getField(7);
            //闸机的交易流水号, 6字节
            setKey.bIn_TradeNo  = setKey.getField(11);
            //响应码
            setKey.bIn_retCode  = retCode;

            //组包
            res = setKey.seal();
            if( res.iResult < 0 )
            {
                log.info("组包发生错误:"+res.iResult);
                return res;
            }
            log.info("组包返回数据:");
            log.info( ISOF.Bytes_HexStr( res.bResult ) );

            return res;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }


    public Map sendAutPreMessage(MessageVO vo){
        Map map = new HashMap(16);
        ConnManager.getInstance().sendMsg(vo);
        PKGResult res1 =new PKGResult();
        PKGResult res2 =new PKGResult();

        WaitResult<String, MessageVO> waitResult = ConnManager.getInstance().getWaitSet().createWaitResult(vo.getKey(),15000);

        WaitResult<String, MessageVO> waitResult2 = ConnManager.getInstance().getWaitSet().createWaitResult("QD002",15000);


        try {
            MessageVO vo2 = waitResult.waitForResult();

            res1.bResult = vo2.getMsgBody();


            map.put("r1",res1);

            MessageVO vo3 = waitResult2.waitForResult();

            res2.bResult = vo3.getMsgBody();

            log.info("已获取第二条密钥信息");

            map.put("r2",res2);

        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return map;
    }
}
