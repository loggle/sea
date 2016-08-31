package com.loggle.rpc.sea.rpc;

/**
 * @author guomy
 * @create 2016-08-30 15:37.
 */
public class HelloWorldImpl implements IHelloWorld {
    public String sayHello(String name) {
        return "hello world " + name;
    }
}
