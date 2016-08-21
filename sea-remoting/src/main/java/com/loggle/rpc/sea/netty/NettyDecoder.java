package com.loggle.rpc.sea.netty;

import com.caucho.hessian.io.HessianInput;
import com.loggle.rpc.common.io.Bytes;
import com.loggle.rpc.common.utils.ReflectUtils;
import com.loggle.rpc.sea.api.Invocation;
import com.loggle.rpc.sea.api.Request;
import com.loggle.rpc.sea.api.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guomy
 * @create 2016-08-05 16:38.
 */
public class NettyDecoder extends ByteToMessageDecoder {
    private byte[] header;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (header != null) {
            decodeBody(in, out);
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

        int oldReadIdx = in.readerIndex();
        ByteBuf body = Unpooled.buffer(bodyLength);
        in.readBytes(body);

        int length = body.readInt();
        String clazz = body.readCharSequence(length, Charset.forName("UTF-8")).toString();
        length = body.readInt();
        String method = body.readCharSequence(length, Charset.forName("UTF-8")).toString();
        length = body.readInt();
        String desc = body.readCharSequence(length, Charset.forName("UTF-8")).toString();

        List<Object> argsList = new ArrayList<Object>();
        while (body.readableBytes() > 0) {
            length = body.readInt();
            ByteBuf temp = Unpooled.buffer(length);
            body.readBytes(temp);
            byte[] bytes = new byte[length];
            temp.getBytes(0, bytes);
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            HessianInput hi = new HessianInput(is);
            argsList.add(hi.readObject());
        }
        Object[] args = argsList.toArray();

        String reqInfo = String.format("reqId=%s, clazz=%s, method=%s, desc=%s, args=%s", reqId, clazz, method, desc, args.toString());
        System.out.println("reqInfo : "+reqInfo);

        Class<?>[] type = ReflectUtils.desc2classArray(desc);

        Request request = new Request();
        request.setId(reqId);

        Invocation invocation = new Invocation();
        invocation.setClazz(clazz);
        invocation.setMethod(method);
        invocation.setParamTypes(type);
        invocation.setArgs(args);

        request.setData(invocation);

        out.add(request);


        return true;
    }

    private boolean decodeHeader(ByteBuf in) {
        if (in.readableBytes() < 15) {
            return false;
        }
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
