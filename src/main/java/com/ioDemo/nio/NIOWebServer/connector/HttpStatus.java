package com.ioDemo.nio.NIOWebServer.connector;

public enum HttpStatus {
    /**
     * ok
     */
    SC_OK(200, "OK"),
    /**
     * not found
     */
    SC_NT_FOUND(404, "NOT FOUND");

    private int status;
    private String msg;

    HttpStatus(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
