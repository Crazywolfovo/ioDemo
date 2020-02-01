package com.ioDemo.bio.BIOMultiThread.Server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yzr
 */
public class ChatServer {

    private final String QUIT = "quit";
    private final int DEFAULT_PORT = 8888;
    private ServerSocket serverSocket = null;
    private ExecutorService executorService;
    /**
     * 多线程共用的变量
     */
    private Map<Integer, Writer> connectedClients;

    public ChatServer() {
        connectedClients = new HashMap<>();
        executorService = Executors.newFixedThreadPool(20);
    }

    public synchronized void addClient(Socket socket) throws IOException {
        if (null != socket) {
            int port = socket.getPort();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClients.put(port, writer);
            System.out.println("客户端{" + port + "}已经链接到服务器");
        }
    }

    public synchronized void removeClient(Socket socket) throws IOException {
        if (null != socket) {
            int port = socket.getPort();
            if (connectedClients.containsKey(port)) {
                connectedClients.get(port).close();
            }
            connectedClients.remove(port);
            System.out.println("客户端{" + port + "}已断开链接");
        }
    }

    public synchronized void forwardMessage(Socket socket, String msg) throws IOException {
        for (Integer id : connectedClients.keySet()) {
            if (!id.equals(socket.getPort())) {
                Writer writer = connectedClients.get(id);
                writer.write(msg);
                writer.flush();
            }
        }
    }

    public boolean readyQuit(String msg) {
        return QUIT.equals(msg);
    }

    public synchronized void close() {
        if (null != serverSocket) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                /**
                 * 创建子线程，处理请求
                 * new Thread(new ChatHandler(this, socket)).start();
                 * 纯BIO，线程创建不受管理，导致线程容易创建过多，
                 * 线程管理和上下文切换都严重消耗性能
                 */
                executorService.execute(new ChatHandler(this, socket));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}
