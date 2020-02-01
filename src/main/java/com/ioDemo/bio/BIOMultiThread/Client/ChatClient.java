package com.ioDemo.bio.BIOMultiThread.Client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final String QUIT = "quit";
    private final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private final int DEFAULT_SERVER_PORT = 8888;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    //发送消息给服务器
    public void send(String msg) throws IOException {
        if (!socket.isOutputShutdown()) {
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    public boolean readyQuit(String msg) {
        return QUIT.equals(msg);
    }

    public void close() {
        if (null != writer) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //创建子线程处理用户输入
            new Thread(new UserInputHandler(this)).start();
            //接受服务器返回的信息
            String msg;
            while (null != (msg = receive())) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
