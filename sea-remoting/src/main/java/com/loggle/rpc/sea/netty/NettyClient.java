package com.loggle.rpc.sea.netty;

import com.loggle.rpc.sea.api.Client;
import com.loggle.rpc.sea.api.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.xml.ws.Response;

/**
 * @author guomy
 * @create 2016-08-05 16:54.
 */
public class NettyClient implements Client{
    private String host;
    private int port;
    private Channel channel;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void send(Request request) {
        if (channel.isActive()) {
            channel.write(request);
        } else {
            System.out.println("channel not active");
        }
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
                            p.addLast(new NettyEncoder(), new NettyDecoder(),
                                    new NettyChannelHandler());
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




}
