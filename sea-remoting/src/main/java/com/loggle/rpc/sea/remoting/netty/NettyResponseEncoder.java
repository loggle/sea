package com.loggle.rpc.sea.remoting.netty;

import com.caucho.hessian.io.HessianOutput;
import com.loggle.rpc.common.io.Bytes;
import com.loggle.rpc.common.utils.ReflectUtils;
import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;
import com.loggle.rpc.sea.remoting.api.Response;
import com.loggle.rpc.sea.remoting.api.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author guomy
 * @create 2016-08-05 16:38.
 */
public class NettyResponseEncoder extends MessageToByteEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {
        byte[] header = new byte[15];
        Bytes.short2bytes(Constants.MAGIC, header);
        header[2] = 0;

        Bytes.long2bytes(response.getId(), header, 3);

        ByteBuf byteBuf = encodeResponseData(response);
        if(byteBuf == null) return;

        Bytes.int2bytes(byteBuf.readableBytes(), header, 11);

        ByteBuf buffer = Unpooled.buffer(header.length + byteBuf.readableBytes());
        buffer.writeBytes(header);
        buffer.writeBytes(byteBuf);
        ctx.writeAndFlush(buffer);

        System.out.println(response.getId() + "  result binary data at " + System.currentTimeMillis());
    }

    private ByteBuf encodeResponseData(Response response) throws IOException {
        ByteBuf bf = Unpooled.buffer();

        Object data = response.getData();
        String str = (String) data;
        bf.writeInt(str.getBytes("UTF-8").length);
        bf.writeBytes(str.getBytes("UTF-8"));
        return bf;
    }
}
