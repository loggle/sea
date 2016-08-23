package com.loggle.rpc.sea.remoting.netty;

import com.loggle.rpc.sea.remoting.api.Client;
import com.loggle.rpc.sea.remoting.api.Server;
import com.loggle.rpc.sea.remoting.api.transport.Transporter;


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
