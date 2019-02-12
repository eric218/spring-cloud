package cn.wimetro.remotecall;

import cn.wimetro.unit.UnionpayUnit;
import com.unionpay.common.communication.onetoone.msgprotocol.IMsg;
import com.unionpay.common.communication.onetoone.msgprotocol.IMsgProtocol;

import java.nio.ByteBuffer;

public class ConnMsgProtocol implements IMsgProtocol {
    @Override
    public boolean isSupportHeartBeat() {
        //搜否支持心跳
        return true;
    }

    @Override
    public long getHeartBeatInterval() {
        //心跳间隔 毫秒 双发需要约定
        return 60000;
    }

    @Override
    public long getDisconnectConfirmTime() {
        //重连的确认时间
        return 180000;
    }

    @Override
    public int getMsgHeadLength() {
        //消息头2字节
        return 2;
    }

    @Override
    public int getBodyLength(ByteBuffer byteBuffer) {
        //
        return UnionpayUnit.bytesToInt(byteBuffer.array());
    }

    @Override
    public IMsg createHeartBeatMsg() {
        //需要新建一个心跳MSg类继承jcom组建中消息基类IMsgs

        return new HeartbeatMsg();
    }
    //根据收到的对端消息的消息头判断是否是心跳消息
    @Override
    public boolean isPeerHeartBeatMsg(IMsg paramIMsg) {
        //判断心跳搜否正确
        if(paramIMsg.getMsgHead() != null){

            int bodyLen = UnionpayUnit.bytesToInt(paramIMsg.getMsgHead());

            if(bodyLen == 0){
                return true;
            }
        }
        return false;
    }




}
