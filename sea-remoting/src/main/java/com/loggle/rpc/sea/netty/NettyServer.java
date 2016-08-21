package com.loggle.rpc.sea.netty;

import com.loggle.rpc.sea.api.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author guomy
 * @create 2016-08-05 15:44.
 */
public class NettyServer  implements Server {
    private ServerBootstrap bootstrap;

    private String host;
    private int port;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean isClose() {
        return false;
    }

    public boolean close() {
        return false;
    }

    public void start() throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(
                                    new NettyEncoder(),
                                    new NettyDecoder(),
                                    new StringEncoder(),
                                    //new TestDecoder(),
                                    new NettyServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = bootstrap.bind(port).sync();
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
