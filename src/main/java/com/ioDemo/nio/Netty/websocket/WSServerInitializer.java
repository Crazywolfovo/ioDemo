package com.ioDemo.nio.Netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author yzr
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //webSocket 基于http协议，需要http编解码器
                .addLast(new HttpServerCodec())
                //大数据流支持
                .addLast(new ChunkedWriteHandler())
                //对httpMessage进行聚合，成FullHttpRequest或者FullHttpResponse
                .addLast(new HttpObjectAggregator(1024 * 64))
                // webSocket 协议路由
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                //自定义webSocket处理助手类
                .addLast(new ChatHandler());
    }
}
