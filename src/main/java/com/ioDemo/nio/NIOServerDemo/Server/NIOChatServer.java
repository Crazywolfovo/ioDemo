package com.ioDemo.nio.NIOServerDemo.Server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * @author yzr
 */
public class NIOChatServer {
    private static final String QUIT = "quit";
    private static final int DEFAULT_PORT = 8888;
    private static final int BUFFER_SIZE = 1024;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer writerBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Charset charset = Charset.forName("UTF-8");
    private int port;

    public NIOChatServer() {
        this(DEFAULT_PORT);
    }

    public NIOChatServer(int port) {
        this.port = port;
    }

    public boolean readyQuit(String msg) {
        return QUIT.equals(msg);
    }

    private void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                //阻塞 直到至少监听到一次监听事件
                selector.select();
                //返回所有被触发的socketChannel
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    //处理触发事件
                    handler(selectionKey);
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(selector);
        }
    }

    private void handler(SelectionKey selectionKey) throws IOException {
        //如果是ServerSocketChannel的accept事件
        if (selectionKey.isAcceptable()) {
            //获取ServerSocketChannel内部包裹的SocketChannel
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            //利用SocketChannel等待客户端的链接
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("客户端[" + socketChannel.socket().getPort() + "]：已经链接");
        }
        //如果是SocketChannel的read读取事件
        else if (selectionKey.isReadable()) {
            //获取ServerSocketChannel内部包裹的
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            String fwdMsg = receive(socketChannel);
            if (fwdMsg.isEmpty()) {
                //客户端链接异常
                selectionKey.cancel();
                selector.wakeup();
            } else {
                //转发数据
                forwardMsg(socketChannel, fwdMsg);
                //检查客户端是否要退出
                if (readyQuit(fwdMsg)) {
                    selectionKey.cancel();
                    selector.wakeup();
                    System.out.println("客户端[" + socketChannel.socket().getPort() + "]：已经退出");
                }
            }
        }

    }

    private String receive(SocketChannel socketChannel) throws IOException {
        readBuffer.clear();
        while (socketChannel.read(readBuffer) > 0) ;
        readBuffer.flip();
        return String.valueOf(charset.decode(readBuffer));
    }

    private void forwardMsg(SocketChannel socketChannel, String fwdMsg) throws IOException {
        //找到目前在线的所有客户端
        //keys返回所有注册的channel
        for (SelectionKey selectionKey : selector.keys()) {
            //跳过ServerSocketChannel，保留SocketChannel
            if (selectionKey.channel() instanceof ServerSocketChannel) {
                continue;
            }
            //检查SocketChannel确保状态正确，且不是产生信息的SocketChannel
            if (selectionKey.isValid() && !socketChannel.equals(selectionKey.channel())) {
                writerBuffer.clear();
                writerBuffer.put(charset.encode("客户端[" + socketChannel.socket().getPort() + "]：" + fwdMsg));
                writerBuffer.flip();
                while (writerBuffer.hasRemaining()) {
                    ((SocketChannel) selectionKey.channel()).write(writerBuffer);
                }
            }
        }
    }

    public static void main(String[] args) {
        NIOChatServer nioChatServer = new NIOChatServer();
        nioChatServer.start();
    }
}
