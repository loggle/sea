package com.loggle.rpc.sea.remoting.api;

/**
 * @author guomy
 * @create 2016-08-05 16:54.
 */
public interface Client {

    void send(Request request);

    void connect() throws InterruptedException, Exception;

    boolean isActive();
}
