package com.ioDemo.aio.echoServer.Client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
    private static final String QUIT = "quit";
    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;
    private AsynchronousSocketChannel asynchronousSocketChannel;

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
            asynchronousSocketChannel = AsynchronousSocketChannel.open();
            Future<Void> future = asynchronousSocketChannel.connect(new InetSocketAddress(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT));
            //阻塞式调用
            future.get();
            //利用future发送消息，等待用户输入消息
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = reader.readLine();
                byte[] bytes = input.getBytes();
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                Future<Integer> writeResult = asynchronousSocketChannel.write(byteBuffer);
                writeResult.get();
                byteBuffer.flip();
                Future<Integer> readResult = asynchronousSocketChannel.read(byteBuffer);
                readResult.get();
                String echo = new String(byteBuffer.array());
                byteBuffer.clear();
                System.out.println(echo);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
