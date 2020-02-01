package com.ioDemo.nio.NIOWebServer;

import com.ioDemo.nio.NIOWebServer.connector.Connector;

public class Bootstrap {
    public static void main(String[] args) {
        Connector connector = new Connector();
        connector.start();

    }
}
