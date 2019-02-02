package cn.wimetro.remotecall;

import cn.wimetro.unit.UnionpayUnit;
import com.unionpay.common.communication.onetoone.msgprotocol.IMsg;

import java.nio.ByteBuffer;


public class MessageVO implements IMsg {
    private String tradType;
    public String key;
    private String s3;
    private String s11;
    private String s12;
    private String s13;
    private String s14;
    private String s15;
    private String s37;
    private String s38;
    private String s39;
    private String s44;
    private String s62;
    private String s63;
    private String s601;
    private String s602;
    private String s603;
    private String s6321;
    private String s6322;
    private String s6323;
    private String s6324;
    private String s6331;

    private byte [] messageHead;
    private byte [] messageBody;

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

        result.rewind();
        return result;
    }

    public String getS12() {
        return s12;
    }

    public void setS12(String s12) {
        this.s12 = s12;
    }

    public String getTradType() {
        return tradType;
    }

    public void setTradType(String tradType) {
        this.tradType = tradType;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }

    public String getS38() {
        return s38;
    }

    public void setS38(String s38) {
        this.s38 = s38;
    }

    public String getS11() {
        return s11;
    }

    public void setS11(String s11) {
        this.s11 = s11;
    }

    public String getS13() {
        return s13;
    }

    public void setS13(String s13) {
        this.s13 = s13;
    }

    public String getS39() {
        return s39;
    }

    public void setS39(String s39) {
        this.s39 = s39;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getS14() {
        return s14;
    }

    public void setS14(String s14) {
        this.s14 = s14;
    }

    public String getS37() {
        return s37;
    }

    public void setS37(String s37) {
        this.s37 = s37;
    }

    public String getS44() {
        return s44;
    }

    public void setS44(String s44) {
        this.s44 = s44;
    }

    public String getS62() {
        return s62;
    }

    public void setS62(String s62) {
        this.s62 = s62;
    }

    public String getS63() {
        return s63;
    }

    public void setS63(String s63) {
        this.s63 = s63;
    }

    public String getS601() {
        return s601;
    }

    public void setS601(String s601) {
        this.s601 = s601;
    }

    public String getS602() {
        return s602;
    }

    public void setS602(String s602) {
        this.s602 = s602;
    }

    public String getS603() {
        return s603;
    }

    public void setS603(String s603) {
        this.s603 = s603;
    }

    public String getS15() {
        return s15;
    }

    public void setS15(String s15) {
        this.s15 = s15;
    }

    public String getS6321() {
        return s6321;
    }

    public void setS6321(String s6321) {
        this.s6321 = s6321;
    }

    public String getS6322() {
        return s6322;
    }

    public void setS6322(String s6322) {
        this.s6322 = s6322;
    }

    public String getS6323() {
        return s6323;
    }

    public void setS6323(String s6323) {
        this.s6323 = s6323;
    }

    public String getS6324() {
        return s6324;
    }

    public void setS6324(String s6324) {
        this.s6324 = s6324;
    }

    public String getS6331() {
        return s6331;
    }

    public void setS6331(String s6331) {
        this.s6331 = s6331;
    }
}
