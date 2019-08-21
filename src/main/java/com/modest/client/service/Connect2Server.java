package com.modest.client.service;

import com.modest.util.CommUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 */
public class Connect2Server {

    private static final String IP;
    private static final int PORT;

    static {
        Properties properties = CommUtils.loadProperties("socket.properties") ;

        IP = properties.getProperty("address");
        PORT = Integer.parseInt(properties.getProperty("port"));
    }

    private Socket client;

    private InputStream in;

    private OutputStream out;


    public Connect2Server() {

        try {
            client = new Socket(IP,PORT);
            in = client.getInputStream();
            out = client.getOutputStream();
        } catch (IOException e) {
            System.out.println("与服务器建立连接失败");
            e.printStackTrace();
        }

    }

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }
}
