package com.ioDemo.nio.Netty.websocket2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WSServer {
    public static void main(String[] args) throws InterruptedException {
        // 1.定义一对线程组
        // bossGroup,用于接受client，不做任何事情
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        // workerGroup，处理bossGroup接受的client
        EventLoopGroup subGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(mainGroup, subGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WSServerInitializer());
            // 3.启动server，绑定端口，启动方式为同步
            ChannelFuture channelFuture = serverBootstrap.bind(8889).sync();
            // 4.监听关闭的channel
            channelFuture.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            subGroup.shutdownGracefully();
        }
    }
}
