package com.ioDemo.nio.NIOWebServer.processor;

import com.ioDemo.nio.NIOWebServer.connector.Request;
import com.ioDemo.nio.NIOWebServer.connector.Response;

import java.io.IOException;

public class StaticProcessor {
    public void process(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
