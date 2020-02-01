package com.ioDemo.nio.NIOWebServer.connector;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

public class Response implements ServletResponse {
    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        InputStream is = getClass().getResourceAsStream(request.getRequestUri());
        try {
            if (null != is) {
                if (request.getRequestUri().contains("/404.html")) {
                    write(is, HttpStatus.SC_NT_FOUND);
                } else {
                    write(is, HttpStatus.SC_OK);
                }
            } else {
                is = getClass().getResourceAsStream("/404.html");
                write(is, HttpStatus.SC_NT_FOUND);
            }
        } catch (IOException e) {
            is = getClass().getResourceAsStream("/404.html");
            write(is, HttpStatus.SC_NT_FOUND);
        }
    }

    private void write(InputStream inputStream, HttpStatus httpStatus) throws IOException {
        outputStream.write(ConnectorUtils.renderStatus(httpStatus).getBytes());
        byte[] bytes = new byte[BUFFER_SIZE];
        int length;
        while (-1 != (length = inputStream.read(bytes, 0, BUFFER_SIZE))) {
            outputStream.write(bytes, 0, length);
        }
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(outputStream, true);
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
