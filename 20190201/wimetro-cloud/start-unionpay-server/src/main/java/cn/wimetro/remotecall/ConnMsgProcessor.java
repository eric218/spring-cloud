package cn.wimetro.remotecall;

import cn.wimetro.constants.Ylstatic;
import cn.wimetro.pos.ISOF;
import cn.wimetro.pos.UnSealBase;
import com.unionpay.common.communication.onetoone.msgprotocol.IMsg;
import com.unionpay.common.communication.onetoone.msgprotocol.IMsgProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnMsgProcessor implements IMsgProcessor {

    //private static Logger log = LoggerFactory.getLogger(ConnMsgProcessor.class);

    /**
     * process里需要将对端信息至少进行初步解析，
     * 从消息体中获取消息的唯一标识，以此标识才能在异步通信框架中对应的请求链路并返回结果
     */
    @Override
    public void process(IMsg rspMsg) {

        log.info("-----ConnMsgProcessor process--------" + ISOF.Bytes_HexStr( rspMsg.getMsgBody() ));

        //拿到密钥
        if(("01").equals(Ylstatic.QIANDAO_FLAG)){

            UnSealBase unsealBase= new UnSealBase();
            unsealBase.unseal(rspMsg.getMsgBody());

            //签到完毕以后设置09 进入刷卡流程
            Ylstatic.QIANDAO_FLAG = "09";
            MessageVO vo = new MessageVO();
            vo.setTradType("002");
            vo.setMsgBody(rspMsg.getMsgBody());

            //log.info("r2==========="+ISOF.Bytes_HexStr(rspMsg.getMsgBody()));
            //唤醒线程
            ConnManager.getInstance().getWaitSet().notifyResult("QD002",vo);
        }

        //第一次
        if(("00").equals(Ylstatic.QIANDAO_FLAG)){
            UnSealBase unsealBase= new UnSealBase();
            unsealBase.unseal(rspMsg.getMsgBody());
            String s11 = unsealBase.getBodyField(11);
            MessageVO vo = new MessageVO();
            vo.setMsgBody(rspMsg.getMsgBody());
            vo.setTradType("QD001");
            //log.info("r1==========="+ISOF.Bytes_HexStr(rspMsg.getMsgBody()));
            //唤醒线程
            vo.setKey("QD001" + s11);
            ConnManager.getInstance().getWaitSet().notifyResult(vo.getKey(),vo);

            //第一次签到完毕以后设置01 进入拿密钥流程
            Ylstatic.QIANDAO_FLAG = "01";
        }

        //预授权 预授权完成
        if(("09").equals(Ylstatic.QIANDAO_FLAG)){

            UnSealBase auth = new UnSealBase();

            auth.unseal(rspMsg.getMsgBody());

            String key;
            //卡号
            MessageVO vo = null;
            try {
                String s2 = auth.getBodyField(2);
                //交易类型
                String s3 = auth.getBodyField(3);

                //流水号
                String s11 = auth.getBodyField(11);
                //交易时间
                String s12 = auth.getBodyField(12);
                //交易日期
                String s13 = auth.getBodyField(13);
                //卡有效期
                String s14 = auth.getBodyField(14);
                //清算日期
                String s15 = auth.getBodyField(15);
                //检索索引号
                String s37 = auth.getBodyField(37);
                //授权码
                String s38 = auth.getBodyField(38);
                //应答码
                String s39 = auth.getBodyField(39);
                //附加响应数据
                String s44 = auth.getBodyField(44);
                //获取第60域
                String s60 = auth.getBodyField(60);
                //获取第62域
                String s62 = auth.getBodyField(62);
                //获取第63域
                String s63 = auth.getBodyField(63);
                String s6321 = null;
                String s6322 = null;
                String s6323 = null;
                String s6324 = null;
                String s6331 = null;

                //交易类型
                String s601 = null;
                //批次号
                String s602 = null;
                //网络管理码
                String s603 = null;

                if(s60!=null && s60.length() >= 11){
                    s601 = s60.substring(0,2);
                    s602 = s60.substring(2,8);
                    s603 = s60.substring(8,11);
                }

                if(s63!=null && s63.length() >= 39){

                    s6321 = s63.substring(3,5);
                    s6322 = s63.substring(5,11);
                    s6323 = s63.substring(11,15);
                    s6324 = s63.substring(15,27);
                    s6331 = s63.substring(27,39);
                }

                vo = new MessageVO();
                //key 为 卡号 流水号
                key = s2 + s11;
                vo.setKey(key);
                vo.setS3(s3);
                vo.setS11(s11);
                vo.setS12(s12);
                vo.setS13(s13);
                vo.setS14(s14);
                vo.setS15(s15);
                vo.setS37(s37);
                vo.setS38(s38);
                vo.setS39(s39);
                vo.setS44(s44);
                vo.setS62(s62);
                vo.setS63(s63);
                vo.setS601(s601);
                vo.setS602(s602);
                vo.setS603(s603);
                vo.setS6321(s6321);
                vo.setS6322(s6322);
                vo.setS6323(s6323);
                vo.setS6324(s6324);
                vo.setS6331(s6331);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ConnManager.getInstance().getWaitSet().notifyResult(vo.getKey(),vo);
        }

    }

}
