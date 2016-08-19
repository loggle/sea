package com.loggle.rpc.sea;

import com.loggle.rpc.sea.api.Server;
import com.loggle.rpc.sea.netty.NettyTransporter;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

/**
 * @author guomy
 * @create 2016-08-05 17:30.
 */
public class ServerDemo {

    public static void main(String[] args) throws Exception {

        URL configURL = Thread.currentThread().getContextClassLoader().getResource("log4j.properties");
        PropertyConfigurator.configure(configURL);

        NettyTransporter transporter = new NettyTransporter();
        Server server = transporter.bind(null, 5600);
        server.start();
    }
}
