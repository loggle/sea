package com.loggle.rpc.sea.netty;

import com.loggle.rpc.common.io.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.xml.ws.Response;
import java.util.List;

/**
 * @author guomy
 * @create 2016-08-18 15:37.
 */
public class TestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 10) {
            return;
        }
        out.add(in.readByte());
    }
}
