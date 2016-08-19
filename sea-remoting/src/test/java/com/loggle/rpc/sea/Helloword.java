package com.loggle.rpc.sea;

/**
 * @author guomy
 * @create 2016-08-19 17:05.
 */
public class Helloword {

    public String sayHi() {

        return "hello word!";
    }

    public String sayHello(String name) {
        System.out.println("hello " + name);
        return "hello " + name;
    }
}
