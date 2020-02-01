package com.ioDemo.nio.NIOWebServer.connector;

public class ConnectorUtils {
    public static final String PROTOCOL = "HTTP/1.1";
    public static final String CARRIAGE = "\r";
    public static final String NEWLINE = "\n";
    public static final String SPACE = " ";

    public static String renderStatus(HttpStatus status) {
        return PROTOCOL +
                SPACE +
                status.getStatus() +
                SPACE +
                status.getMsg() +
                CARRIAGE + NEWLINE +
                CARRIAGE + NEWLINE;
    }

}
