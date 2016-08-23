package com.loggle.rpc.sea.remoting.api.transport;

import com.loggle.rpc.sea.remoting.api.Client;
import com.loggle.rpc.sea.remoting.api.Server;

/**
 * @author guomy
 * @create 2016-08-05 15:37.
 */
public interface Transporter {

    public Server bind(String host, int port) throws Exception;

    public Client connect(String host, int port);
}
