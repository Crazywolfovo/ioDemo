package com.ioDemo.aio.echoServer.Server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yzr
 */
public class Server {
    private static final String QUIT = "quit";
    private static final int DEFAULT_PORT = 8888;

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    private void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(DEFAULT_PORT));
            System.out.println("服务器启动,监听端口:" + DEFAULT_PORT);
            while (true) {
                asynchronousServerSocketChannel.accept(null, new AcceptHandler());
                // 避免循环过于频繁的调用；避免主线程提前退出
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(asynchronousServerSocketChannel);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }


    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {
        @Override
        public void completed(AsynchronousSocketChannel result, Object attachment) {
            if (asynchronousServerSocketChannel.isOpen()) {
                asynchronousServerSocketChannel.accept(null, this);
            }
            if (null != result && result.isOpen()) {
                ClientHandler clientHandler = new ClientHandler(result);

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                Map<String, Object> info = new HashMap<>();
                info.put("type", "read");
                info.put("buffer", byteBuffer);

                result.read(byteBuffer, info, clientHandler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // 处理错误
        }
    }

    private class ClientHandler implements CompletionHandler<Integer, Object> {
        AsynchronousSocketChannel asynchronousSocketChannel;

        public ClientHandler(AsynchronousSocketChannel result) {
            this.asynchronousSocketChannel = result;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            Map<String, Object> info = (Map<String, Object>) attachment;
            String type = (String) info.get("type");
            // 读取客户端发送的数据
            if ("read".equals(type)) {
                ByteBuffer byteBuffer = (ByteBuffer) info.get("buffer");
                byteBuffer.flip();
                info.put("type", "write");
                // 将数据返回给客户端
                asynchronousSocketChannel.write(byteBuffer, info, this);
                byteBuffer.clear();
            } else if ("write".equals(type)) {
                //继续监听客户端发送来的数据
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                info.put("type", "read");
                info.put("buffer", byteBuffer);

                asynchronousSocketChannel.read(byteBuffer, info, this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }
}
