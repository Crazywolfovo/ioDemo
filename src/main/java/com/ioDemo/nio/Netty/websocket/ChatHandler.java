package com.ioDemo.nio.Netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

/**
 * 处理消息的handler，
 * TextWebSocketFrame：在netty中，专门用于为webSocket处理文本的对象，frame是消息的载体
 *
 * @author yzr
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 利用ChannelGroup  管理全局的客户端
     */
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //获取客户端传输过来的消息
        System.out.println("接受到的数据：" + msg.text());
        //转发到所有客户端
        /*for (Channel client : clients) {
            client.writeAndFlush(
                    new TextWebSocketFrame(
                            LocalDateTime.now() + "服务器接收到消息 : " + msg.text()));
        }*/
        clients.writeAndFlush(new TextWebSocketFrame(LocalDateTime.now() + "服务器接收到消息 : " + msg.text()));
    }

    /**
     * 当客户端链接服务器之后
     * 获取客户端channel并放到ChannelGroup中进入管理
     *
     * @param ctx 对象
     * @throws Exception 异常
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        //clients.remove(ctx.channel());
        System.out.println("客户端断开，长ID为：[" + ctx.channel().id().asLongText() + "]");
        System.out.println("客户端断开，短ID为：[" + ctx.channel().id().asShortText() + "]");
    }
}
