package com.loggle.rpc.sea.remoting.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author guomy
 * @create 2016-08-05 16:18.
 */
public class NettyChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf in = (ByteBuf) msg;
        try {
            /*while (in.isReadable()) { // (1)
                System.out.print((char) in.readByte());
                System.out.flush();
            }*/

            Byte b = (Byte) msg;
            System.out.print((char)b.byteValue());
            System.out.flush();
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }
}
