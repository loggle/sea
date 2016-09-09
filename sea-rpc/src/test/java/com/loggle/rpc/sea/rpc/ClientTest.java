package com.loggle.rpc.sea.rpc;

import com.loggle.rpc.sea.rpc.proxy.RpcProxyBuilder;
import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guomy
 * @create 2016-08-30 15:38.
 */
public class ClientTest extends TestCase {

    RpcProxyBuilder rpcProxyBuilder;
    Executor executor;

    public void setUp() throws Exception {
        super.setUp();

        rpcProxyBuilder = new RpcProxyBuilder(5600, "127.0.0.1");
        rpcProxyBuilder.init();

        executor = Executors.newFixedThreadPool(20);
    }

    public void testClient() throws Exception {
        System.out.println("testClient start...");
        final IHelloWorld c = (IHelloWorld) rpcProxyBuilder.getProxy(IHelloWorld.class);
        final CountDownLatch countDownLatch = new CountDownLatch(100);

        for(int j=0; j<2; j++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        for(int i=0; i<10; i++) {
                            String loggle = c.sayHello("loggle_" + i);
//
//                            try {
//                                Thread.sleep(1000 * 30);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            System.out.println("server result: " + loggle);
                        }
                    } finally {
                        countDownLatch.countDown();
                    }

                }
            });
            executor.execute(thread);
        }

        countDownLatch.await();

    }

    public void tearDown() throws Exception {
        super.tearDown();
    }
}
