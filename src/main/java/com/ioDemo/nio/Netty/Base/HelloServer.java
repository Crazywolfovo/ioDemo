package com.ioDemo.nio.Netty.Base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 主从Reactor模式
 *
 * @author yzr
 */
public class HelloServer {
    public static void main(String[] args) throws InterruptedException {
        // 1.定义一对线程组
        // bossGroup,用于接受client，不做任何事情
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // workerGroup，处理bossGroup接受的client
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 2.创建启动类实例，设置线程组
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    //设置nio的channel
                    .channel(NioServerSocketChannel.class)
                    //设置channel的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //通过管道添加handler,当请求到服务端，做编解码
                            socketChannel.pipeline()
                                    .addLast("HttpServerCodec", new HttpServerCodec())
                                    .addLast("CustomizedHandler", new CustomizedHandler());
                        }
                    });
            // 3.启动server，绑定端口，启动方式为同步
            ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();
            // 4.监听关闭的channel
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
