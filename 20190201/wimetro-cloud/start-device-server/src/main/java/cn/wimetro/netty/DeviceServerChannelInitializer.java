package cn.wimetro.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class DeviceServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        // 解码编码
//        socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
//        socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));

        socketChannel.pipeline().addLast(new DeviceServerHandler());
    }
}
