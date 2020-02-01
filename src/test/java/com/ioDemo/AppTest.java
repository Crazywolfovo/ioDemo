package com.ioDemo;

import com.ioDemo.nio.NIOWebServer.connector.Request;
import com.ioDemo.nio.NIOWebServer.connector.Response;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final String uri = "GET /index.html HTTP/1.1";
    private static final String uri2 = "GET /404.html HTTP/1.1";

    private static final String status200 = "HTTP/1.1 200 OK\r\n\r\n";
    private static final String status404 = "HTTP/1.1 404 NOT FOUND\r\n\r\n";

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void validRequest() {
        Assert.assertEquals("/index.html", TestUtils.createRequest(uri).getRequestUri());
    }

    @Test
    public void validResponse() throws IOException {
        Request request = TestUtils.createRequest(uri2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Response response = new Response(byteArrayOutputStream);
        response.setRequest(request);
        response.sendStaticResource();

        InputStream is = getClass().getResourceAsStream(request.getRequestUri());
        Assert.assertEquals(status404 + TestUtils.readFileToString(is), byteArrayOutputStream.toString());
    }
}
