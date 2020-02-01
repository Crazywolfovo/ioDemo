package com.ioDemo;

import com.ioDemo.nio.NIOWebServer.connector.Request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestUtils {
    public static Request createRequest(String ctx) {
        InputStream inputStream = new ByteArrayInputStream(ctx.getBytes());
        Request request = new Request(inputStream);
        request.parse();
        return request;
    }

    public static String readFileToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}