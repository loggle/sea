package com.loggle.rpc.sea.remoting.netty;

import com.caucho.hessian.io.HessianInput;
import com.loggle.rpc.common.io.Bytes;
import com.loggle.rpc.common.utils.ReflectUtils;
import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;
import com.loggle.rpc.sea.remoting.api.Response;
import com.loggle.rpc.sea.remoting.api.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guomy
 * @create 2016-08-05 16:38.
 */
public class NettyResponseDecoder extends ByteToMessageDecoder {
    private byte[] header;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (header != null) {
            if(decodeBody(in, out)) {
                header = null;
            }
            return;
        }

        if(!decodeHeader(in)) {
            return;
        }
        if(decodeBody(in, out)) {
            header = null;
        }
    }

    private boolean decodeBody(ByteBuf in, List<Object> out) {
        int oldReadIdx = in.readerIndex();
        int bodyLength = Bytes.bytes2int(header, 11);
        try {
            return decodeBody0(in, out);
        } catch (Exception e) {
            System.out.println("decode body error!");
            e.printStackTrace();
            System.out.println("decode body error! discard this message!");
            in.readerIndex(oldReadIdx + bodyLength);
            return true;
        } finally {
        }
    }
    private boolean decodeBody0(ByteBuf in, List<Object> out) throws IOException, ClassNotFoundException {
        int bodyLength = Bytes.bytes2int(header, 11);
        long reqId = Bytes.bytes2long(header, 3);
        if(in.readableBytes() < bodyLength) {
            return false;
        }

        ByteBuf body = Unpooled.buffer(bodyLength);
        in.readBytes(body);

        int length = body.readInt();
        byte[] bytes = new byte[length];
        body.readBytes(bytes, 0, length);

        String msg = new String(bytes, "UTF-8");

        String reqInfo = String.format("respId=%s, msg=%s", reqId, msg);
        System.out.println("reqInfo : "+reqInfo);


        Response response = new Response();
        response.setId(reqId);
        response.setData(msg);
        out.add(response);


        return true;
    }

    private boolean decodeHeader(ByteBuf in) {
        if (in.readableBytes() < 15) {
            return false;
        }
        System.out.println("**************************************************************" + Thread.currentThread().getId() + "--" + Thread.currentThread().getName());
        int oldReadIdx = in.readerIndex();
        short magic = in.readShort();
        while(magic != Constants.MAGIC) {//循环读取，直到能读取出包头
            in.readerIndex(oldReadIdx + 1);//从下一个字节开始读
            if(in.readableBytes() < 15) {//不足一个包头的数据，直接返回
                return false;
            }
            oldReadIdx = in.readerIndex();
            magic = in.readShort();
        }

        byte flag = in.readByte();

        long reqId = in.readLong();

        int bodyLength = in.readInt();

        header = new byte[15];
        Bytes.short2bytes(magic, header);
        header[2] = flag;
        Bytes.long2bytes(reqId, header, 3);
        Bytes.int2bytes(bodyLength, header, 11);
        return true;
    }
}
