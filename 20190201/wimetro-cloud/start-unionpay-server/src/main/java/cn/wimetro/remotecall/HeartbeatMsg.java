package cn.wimetro.remotecall;

import cn.wimetro.unit.UnionpayUnit;
import com.unionpay.common.communication.onetoone.msgprotocol.IMsg;

import java.nio.ByteBuffer;


public class HeartbeatMsg implements IMsg {

    private byte [] messageHead;
    private byte [] messageBody;

    public HeartbeatMsg(){
        messageBody = new byte[0];
    }

    @Override
    public byte[] getMsgHead() {

        return messageHead;
    }

    @Override
    public void setMsgHead(byte[] head) {
            this.messageHead = head;
    }

    @Override
    public byte[] getMsgBody() {
        return messageBody;
    }

    @Override
    public void setMsgBody(byte[] body) {
        this.messageBody = body;
    }

    @Override
    public ByteBuffer toByteBuffer() {

        ByteBuffer result = ByteBuffer.allocate(2 + this.getMsgBody().length);
        String bodyLen = String.format("%04x", this.getMsgBody().length)
                .toUpperCase();
        this.messageHead = UnionpayUnit.hex2byte(bodyLen);
        result.put(this.getMsgHead());
        if( this.getMsgBody().length != 0){
            result.put(this.getMsgBody());
        }
        //重新置到头部
        result.rewind();
        return result;

    }
}
