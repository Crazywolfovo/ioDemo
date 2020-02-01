package com.ioDemo.bio.BIOMultiThread.Server;

import java.io.*;
import java.net.Socket;

/**
 * @author yzr
 */
public class ChatHandler implements Runnable {
    private final String QUIT = "quit";
    private ChatServer chatServer;
    private Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            chatServer.addClient(socket);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String msg;
            while ((msg = reader.readLine()) != null) {
                String fwdMsg = "客户端[" + socket.getPort() + "]：" + msg + "\n";
                System.out.println(fwdMsg);
                chatServer.forwardMessage(socket, fwdMsg);
                if (chatServer.readyQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
