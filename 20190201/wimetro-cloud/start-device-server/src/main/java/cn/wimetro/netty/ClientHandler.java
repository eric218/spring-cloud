package cn.wimetro.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) {
        channelHandlerContext.channel().attr(AttributeKey.valueOf("Attribute_key")).set(msg);
        channelHandlerContext.close();
    }
}