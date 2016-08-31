package com.loggle.rpc.sea.remoting.netty;

import com.loggle.rpc.sea.remoting.api.Client;
import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author guomy
 * @create 2016-08-05 16:54.
 */
public class NettyClient implements Client {
    private String host;
    private int port;
    private Channel channel;

    private static Map<Long, Future> futureMap = new ConcurrentHashMap<Long, Future>();

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void send(Request request) {
        if (channel.isActive()) {
            synchronized (channel) {
                channel.write(request);
            }
        } else {
            System.out.println("channel not active");
        }
    }

    public Object sendAndWait(Request request) {
        if (channel.isActive()) {
            synchronized (channel) {
                futureMap.put(request.getId(), new CallBack(request));
                channel.write(request);
            }
        } else {
            System.out.println("channel not active");
        }
        Future future = NettyClient.getFuture(request.getId());

        try {
            try {
                return future.get(20, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Future getFuture(long reqId) {
        return futureMap.get(reqId);
    }

    public void connect() throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new NettyEncoder(), new NettyResponseDecoder(),
                                    new NettyClientHandler2());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            channel = f.channel();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }
        finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public boolean isActive() {
        if (channel == null) {
            return false;
        }
        return channel.isActive();
    }


    class CallBack implements Future {

        private Sync sync;

        private Request request;

        private String result;

        public CallBack(Request request) {
            this.sync = new Sync();
            this.request = request;
        }

        public void done(String msg) {
            this.result = msg;
            sync.release(1);
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }

        public boolean isCancelled() {
            throw new UnsupportedOperationException();
        }

        public boolean isDone() {
            return sync.isDone();
        }

        public Object get() throws InterruptedException, ExecutionException {
            sync.acquire(-1);
            return result;
        }

        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));

            if(success){
                return result;
            }else{
                throw new RuntimeException("Timeout exception|reqId="+request.getId()+"|funcName="+((Invocation)request.getData()).getMethod());
            }
        }

        class Sync extends AbstractQueuedSynchronizer {

            private static final long serialVersionUID = 1L;

            //future status
            private final int done = 1;
            private final int pending = 0;

            protected boolean tryAcquire(int acquires) {
                return getState()==done?true:false;
            }

            protected  boolean tryRelease(int releases) {
                if (getState() == pending) {
                    if (compareAndSetState(pending, 1)) {
                        return true;
                    }
                }
                return false;
            }

            public boolean isDone(){
                getState();
                return getState()==done;
            }
        }
    }


}
