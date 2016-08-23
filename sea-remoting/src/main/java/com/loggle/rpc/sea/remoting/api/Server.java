package com.loggle.rpc.sea.remoting.api;

/**
 * @author guomy
 * @create 2016-08-05 15:39.
 */
public interface Server {

    boolean isClose();

    boolean close();

    void start() throws InterruptedException, Exception;
}
