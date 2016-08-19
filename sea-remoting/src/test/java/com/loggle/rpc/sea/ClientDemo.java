package com.loggle.rpc.sea;

import com.loggle.rpc.sea.api.Client;
import com.loggle.rpc.sea.api.Invocation;
import com.loggle.rpc.sea.api.Request;
import com.loggle.rpc.sea.netty.NettyTransporter;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

/**
 * @author guomy
 * @create 2016-08-05 17:33.
 */
public class ClientDemo {

    private static  Client client;

    public static void main(String[] args) throws Exception {
        URL configURL = Thread.currentThread().getContextClassLoader().getResource("log4j.properties");
        PropertyConfigurator.configure(configURL);

        new Thread(new Runnable() {
            public void run() {
                NettyTransporter transporter = new NettyTransporter();
                client = transporter.connect("127.0.0.1", 5600);
                try {
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(3000);

        System.out.println("start send...");
        Invocation invocation = new Invocation();
        invocation.setClazz(Helloword.class.getName());

        Class[] type = {String.class};
        invocation.setParamTypes(type);
        try {
            invocation.setMethod(Helloword.class.getMethod("sayHello", String.class).getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Object[] args2 = {"fdas"};
        invocation.setArgs(args2);

        Request request = new Request();
        request.setId(1);
        request.setData(invocation);

        client.send(request);

        System.out.println("end send...");

        System.out.println("waite...");
        Thread.sleep(60000);
        System.out.println("end！！！");


    }
}
