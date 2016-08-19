package com.loggle.rpc.sea.netty;

import com.loggle.rpc.sea.api.Client;
import com.loggle.rpc.sea.api.Server;
import com.loggle.rpc.sea.api.transport.Transporter;


/**
 * @author guomy
 * @create 2016-08-05 15:43.
 */
public class NettyTransporter implements Transporter {
    public Server bind(String host, int port) throws Exception {
        NettyServer server = new NettyServer(host, port);
        return server;
    }

    public Client connect(String host, int port) {
        return new NettyClient(host, port);
    }
}
