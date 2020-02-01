package com.ioDemo.bio.BIOBaseDemo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author yzr
 */
public class Server {
    public static void main(String[] args) {
        final String QUIT = "quit";
        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;
        try {
            //绑定监听端口
            serverSocket = new ServerSocket(DEFAULT_PORT);
            while (true) {
                //在主线程中等待客户端链接
                Socket socket = serverSocket.accept();
                //创建读写socket的IO工具 reader和writer
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                //读取客户端信息
                String msg;
                while ((msg = reader.readLine()) != null) {
                    System.out.println("客户端--" + socket.getPort() + "：" + msg);
                    //服务器返回去消息
                    writer.write("服务器：" + msg + "\n");
                    writer.flush();
                    //检查客户端退出
                    if (QUIT.equals(msg)) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != serverSocket) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
