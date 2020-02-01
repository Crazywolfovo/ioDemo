package com.ioDemo.nio.NIOServerDemo.Client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

public class NIOChatClient {
    private static final String QUIT = "quit";
    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;
    private static final int BUFFER_SIZE = 1024;

    private String host;
    private int port;
    private SocketChannel socketChannel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer writerBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Selector selector;
    private Charset charset = Charset.forName("UTF-8");

    public NIOChatClient() {
        this(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
    }

    public NIOChatClient(String host, int port) {
        this.host = host;
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
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(host, port));

            while (true) {
                selector.select();
                //获取被触发的socketChannel
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    //处理触发事件
                    handler(selectionKey);
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClosedSelectorException e) {
            System.out.println("成功断开链接");
        } finally {
            close(selector);
        }
    }

    private void handler(SelectionKey selectionKey) throws IOException {
        // CONNECT 建立链接
        if (selectionKey.isConnectable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            // true链接就绪；false继续等待
            if (socketChannel.isConnectionPending()) {
                socketChannel.finishConnect();
                //额外的线程去处理用户输入的信息
                new Thread(new UserInputHandler(this)).start();
            }
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
        // READ事件：服务器转发消息
        else if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            String msg = receive(socketChannel);
            if (msg.isEmpty()) {
                // 服务器异常,客户端退出
                close(selector);
            } else {
                System.out.println(msg);
            }
        }
    }

    private String receive(SocketChannel socketChannel) throws IOException {
        readBuffer.clear();
        while (socketChannel.read(readBuffer) > 0) ;
        readBuffer.flip();
        return String.valueOf(charset.decode(readBuffer));
    }

    //发送消息给服务器
    public void send(String msg) throws IOException {
        if (msg.isEmpty()) {
            return;
        }
        writerBuffer.clear();
        writerBuffer.put(charset.encode(msg));
        writerBuffer.flip();
        while (writerBuffer.hasRemaining()) {
            socketChannel.write(writerBuffer);
        }
        if (readyQuit(msg)) {
            close(selector);
        }
    }

    public static void main(String[] args) {
        NIOChatClient nioChatClient = new NIOChatClient();
        nioChatClient.start();
    }
}

