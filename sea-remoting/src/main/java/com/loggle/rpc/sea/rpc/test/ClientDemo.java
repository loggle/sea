package com.loggle.rpc.sea.rpc.test;

import com.loggle.rpc.sea.remoting.api.Client;
import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;
import com.loggle.rpc.sea.remoting.netty.NettyTransporter;
import com.loggle.rpc.sea.rpc.test.rpc.IHelloWorld;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guomy
 * @create 2016-08-05 17:33.
 */
public class ClientDemo {

    private static  Client client;

    private static Executor executor = Executors.newFixedThreadPool(8, new ThreadFactory() {
        AtomicInteger count = new AtomicInteger(0);
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("sendThread-" + count.incrementAndGet());
            return thread;
        }
    });

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

        while (client == null || !client.isActive()) {
            Thread.sleep(80);
            continue;
        }

        int limit = 30;
        final AtomicInteger count = new AtomicInteger(0);
        for (int i=0; i< limit; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    send(count);
                }
            });

        }

    }

    private static void send(AtomicInteger count) {
        int index = count.incrementAndGet();
        System.out.println(index + "--start send...");
        Invocation invocation = new Invocation();
        invocation.setClazz(IHelloWorld.class.getName());

        Class[] type = {String.class};
        invocation.setParamTypes(type);
        try {
            invocation.setMethod(IHelloWorld.class.getMethod("sayHello", String.class).getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Object[] args2 = {"fdas"};
        invocation.setArgs(args2);

        Request request = new Request();
        request.setId(index);
        request.setData(invocation);

        client.send(request);

        System.out.println(index + " - send end...");
    }
}
