package com.loggle.rpc.sea.rpc.proxy;


import com.loggle.rpc.sea.remoting.api.Client;
import com.loggle.rpc.sea.remoting.netty.NettyTransporter;

import java.lang.reflect.Proxy;

/**
 * @author guomy
 * @create 2016-08-30 14:18.
 */
public class RpcProxyBuilder {

    private int serverPort;
    private String serverHost;


    Client client;

    public RpcProxyBuilder(int serverPort, String serverHost) {
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    public void init() {
        new Thread(new Runnable() {
            public void run() {
                NettyTransporter transporter = new NettyTransporter();
                client = transporter.connect(serverHost, serverPort);
                try {
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Object getProxy(Class clazz) throws InterruptedException {
        while (client == null || !client.isActive()) {
            Thread.sleep(100);
        }

        Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new RpcProxy(client, clazz));
        return proxy;
    }
}
