package com.loggle.rpc.sea.remoting.netty;

import com.caucho.hessian.io.HessianOutput;
import com.loggle.rpc.common.io.Bytes;
import com.loggle.rpc.common.utils.ReflectUtils;
import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;
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
public class NettyEncoder extends MessageToByteEncoder<Request> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Request request, ByteBuf out) throws Exception {
        byte[] header = new byte[15];
        Bytes.short2bytes(Constants.MAGIC, header);
        header[2] = 0;

        Bytes.long2bytes(request.getId(), header, 3);

        ByteBuf byteBuf = encodeRequestData(request);
        if(byteBuf == null) return;

        Bytes.int2bytes(byteBuf.readableBytes(), header, 11);

        ByteBuf buffer = Unpooled.buffer(header.length + byteBuf.readableBytes());
        buffer.writeBytes(header);
        buffer.writeBytes(byteBuf);
        ctx.writeAndFlush(buffer);
    }

    private ByteBuf encodeRequestData(Request request) throws IOException {
        Object data = request.getData();
        if (data instanceof Invocation) {
            Invocation invocation = (Invocation) data;
            String clazz = invocation.getClazz();
            String method = invocation.getMethod();
            String desc = ReflectUtils.getDesc(invocation.getParamTypes());

            ByteBuf bf = Unpooled.buffer();
            bf.writeInt(clazz.getBytes("UTF-8").length);
            bf.writeBytes(clazz.getBytes("UTF-8"));

            bf.writeInt(method.getBytes("UTF-8").length);
            bf.writeBytes(method.getBytes("UTF-8"));

            bf.writeInt(desc.getBytes("UTF-8").length);
            bf.writeBytes(desc.getBytes("UTF-8"));

            Object[] args = invocation.getArgs();
            if (args != null && args.length > 0) {
                for (Object obj : args) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    HessianOutput ho = new HessianOutput(os);
                    ho.writeObject(obj);
                    bf.writeInt(os.toByteArray().length);
                    bf.writeBytes(os.toByteArray());
                }
            } else {
            }

            return bf;
        }
        return null;
    }
}
