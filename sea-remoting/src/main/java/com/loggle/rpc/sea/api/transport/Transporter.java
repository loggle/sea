package com.loggle.rpc.sea.api.transport;

import com.loggle.rpc.sea.api.Client;
import com.loggle.rpc.sea.api.Server;

/**
 * @author guomy
 * @create 2016-08-05 15:37.
 */
public interface Transporter {

    public Server bind(String host, int port) throws Exception;

    public Client connect(String host, int port);
}
