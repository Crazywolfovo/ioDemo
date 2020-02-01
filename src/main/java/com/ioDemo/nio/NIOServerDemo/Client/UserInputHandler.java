package com.ioDemo.nio.NIOServerDemo.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInputHandler implements Runnable {

    private NIOChatClient chatClient;

    public UserInputHandler(NIOChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        //等待用户输入消息
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String input = null;
            try {
                input = consoleReader.readLine();
                chatClient.send(input);
                if (chatClient.readyQuit(input)) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
