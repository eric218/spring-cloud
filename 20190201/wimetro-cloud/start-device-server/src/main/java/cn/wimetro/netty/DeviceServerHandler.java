package cn.wimetro.netty;

import cn.wimetro.unit.SecurityUnit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
/**
 * netty 业务处理类
 * @author wangwei
 * @date  2019-02-13
 * @param
 * @return
 **/
@Slf4j
public class DeviceServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 收到数据时调用
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf in = (ByteBuf)msg;
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String clientIP = insocket.getAddress().getHostAddress();
            log.info("channelRead clientIP:" + clientIP);

            String readIn = in.toString(CharsetUtil.UTF_8);
            log.info("channelRead 接受到数据"+readIn);
            String readType = getBizType(readIn);
            //old
            //RTC004_RES010000201812291036360063160731073143160001491600000200201812281955256224242200000052FFFA190
//            String returnCode = "RTC004_RES010000201812291036360063160731073143160001491600000200201812281955256224242200000052FFF";
//            String crc = getCRC16(returnCode);
//            returnCode = returnCode + crc;
            String returnCode = getRetuenCode(readType);
            returnCode = returnCode+readIn;
            ctx.writeAndFlush(getSendByteBuf(returnCode));
            log.info("已发送"+ returnCode);
        } finally {
            // 抛弃收到的数据
            ReferenceCountUtil.release(msg);
        }

//        ctx.write(msg);
//        ctx.flush();
    }

    private String getBizType(String readIn){
        String type="";
        if(readIn.length()<=48){
            type = "";
        }else{
            //RTC003_REQ0101174309RTC0032018122914383041010464011743090100003584011726020600150007046236030100000001000398000000000000000020181229143830000000000000000000000000000000000000000000000083000117430920181229143830400000000533F10000000001196236030100000001533D27122202995800000F01179F2608CC731B1CFB3B34089F2701809F101307010103A00000010A01000000000076135C999F3704A4BBA9FD9F3602018E950500000000009A031812299C01039F02060000000012005F2A02015682027C009F1A0201569F03060000000000009F33030008C09F3501259F1E083030303033353834B7F7
            if(readIn.substring(0,6).equals("RTC003")){
                type = "RTC003";
            }
            //RTC004_REQ0101174309RTC004201812291436090301001962360301000000015331CC9
            if(readIn.substring(0,6).equals("RTC004")){
                type = "RTC004";
            }
            //RTC005_REQ0101174309RTC00520181229143924030105892023100000000001174309010000358601171702060015000888623603010000000100040000000000000000002018122914392407314316000149160000020020181228195525DB443C770000000000000000000000028300000000000000000000000000202712270000000000000000000000004123456789abcdef123456789ABCDEF123456789abcdef9876500000000533F00000000000001196236030100000001533D27122202995800000F01179F2608F0C4234FDF653E619F2701809F101307010103A00000010A0100000000008878907D9F3704AEF51BE19F36020190950500000000009A031812299C01039F02060000000012005F2A02015682027C009F1A0201569F03060000000000009F33030008C09F3501259F1E083030303033353834AB3A
            if(readIn.substring(0,6).equals("RTC005")){
                type = "RTC005";
            }
        }

        log.info("交易类型:" + type);
        return type;
    }

    private String getRetuenCode(String bizType){
        String returnCode = "";
        //RTC004_RES010000201812291036360063160731073143160001491600000200201812281955256224242200000052FFFA190
        if("RTC004".equals(bizType)){
            returnCode = "RTC004_RES010000201812291036360063160731073143160001491600000200201812281955256224242200000052FFF";
            String crc = SecurityUnit.getCRC16(returnCode);
            returnCode = returnCode + crc;
        }
        //RTC005_RES0100002018122719375600005F68
        if("RTC005".equals(bizType)){
            returnCode = "RTC005_RES010000201812271937560000";
            String crc = SecurityUnit.getCRC16(returnCode);
            returnCode = returnCode + crc;
        }

        //RTC003_RES010000201812271937560180073643050000114161073627020600040014776214832719291465000000000002000000000020181227193739083843190005672700000000201812271909390000000083000838431920181227190939400000000FFFF1100077C3
        if("RTC003".equals(bizType)){
            returnCode = "RTC003_RES010000201812271937560180073643050000114161073627020600040014776214832719291465000000000002000000000020181227193739083843190005672700000000201812271909390000000083000838431920181227190939400000000FFFF11000";
            String crc = SecurityUnit.getCRC16(returnCode);
            returnCode = returnCode + crc;
        }
        if("".equals(bizType)){
            returnCode = "sent empty package";
        }

        return returnCode;
    }

    private ByteBuf getSendByteBuf(String message){

        ByteBuf pingMessage = null;
        try {
            byte[] req = message.getBytes("UTF-8");
            pingMessage = Unpooled.buffer();
            pingMessage.writeBytes(req);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return pingMessage;
    }

    @Override
    public void  channelRegistered(ChannelHandlerContext ctx){
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        log.info("channelRegistered tcp 获取链接:" + clientIP );
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        log.info("channelUnregistered tcp 断开连接:" + clientIP);
    }

    /**
     * 当Netty由于IO错误或者处理器在处理事件时抛出异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
