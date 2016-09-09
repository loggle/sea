package com.loggle.rpc.sea.rpc.test;

import com.loggle.rpc.sea.rpc.proxy.RpcProxyBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guomy
 * @create 2016-08-30 15:38.
 */
public class ClientTest {

    static RpcProxyBuilder rpcProxyBuilder;
    static Executor executor;

    public static void setUp() throws Exception {

        rpcProxyBuilder = new RpcProxyBuilder(5600, "127.0.0.1");
        rpcProxyBuilder.init();

        executor = Executors.newFixedThreadPool(20);
    }

    public static void testClient() throws Exception {
        System.out.println("testClient start...");

        int maxJ = 10;
        final int maxI = 100;
        final IHelloWorld c = (IHelloWorld) rpcProxyBuilder.getProxy(IHelloWorld.class);
        final CountDownLatch countDownLatch = new CountDownLatch(maxJ);

        long allstart = System.currentTimeMillis();
        for(int j=0; j<maxJ; j++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        for(int i=0; i<maxI; i++) {
                            long start = System.currentTimeMillis();
                            System.out.println(">>>>>>>>>>>before call method at " + System.currentTimeMillis());
                            String loggle = c.sayHello("loggle_" + i);

                            //System.out.println("server result: " + loggle + ">>>>>>>>>>  in " + (System.currentTimeMillis() - start) + " ms .");
                        }
                    } finally {
                        countDownLatch.countDown();
                    }

                }
            });
            thread.start();
            //executor.execute(thread);
        }

        countDownLatch.await();

        System.out.println("************** all end nend time " + (System.currentTimeMillis() - allstart) + " ms .");

        executor = null;

        System.out.println("******************testClient end xxxxxxxxxxxxx...");
    }

    public static void main(String[] args) throws Exception {
        setUp();
        testClient();
    }
}
