package com.ioDemo.nio.NIOWebServer.processor;

import com.ioDemo.nio.NIOWebServer.connector.Request;
import com.ioDemo.nio.NIOWebServer.connector.Response;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ServletProcessor {

    /**
     * 反射技术 加载类创建实例
     */
    public void process(Request request, Response response) {
        try {
            Servlet servlet = getServlet(request);
            servlet.service(request, response);
        } catch (IOException | InstantiationException | ServletException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Servlet getServlet(Request request) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String uri = request.getRequestUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        Class servlet = Class.forName("com.ioDemo.nio.NIOWebServer.business." + servletName);
        return (Servlet) servlet.newInstance();
    }

    private Servlet getServlet(URLClassLoader urlClassLoader, Request request) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String uri = request.getRequestUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        Class servletClass = urlClassLoader.loadClass(servletName);
        return (Servlet) servletClass.newInstance();
    }

    private URLClassLoader getServletLoader() throws MalformedURLException {
        String url = ServletProcessor.class.getClassLoader().getResource("").toString();
        return new URLClassLoader(new URL[]{new URL(url)});
    }

}
