package com.loggle.rpc.sea.remoting.netty;

import com.caucho.hessian.io.HessianInput;
import com.loggle.rpc.common.fileio.WriteToFile;
import com.loggle.rpc.common.io.Bytes;
import com.loggle.rpc.common.utils.ReflectUtils;
import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;
import com.loggle.rpc.sea.remoting.api.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author guomy
 * @create 2016-08-05 16:38.
 */
public class NettyDecoder extends ByteToMessageDecoder {
    private byte[] header;

    private static WriteToFile reciFile;

    static {
        reciFile = new WriteToFile("e:/test/reciFile2.log");
        try {
            reciFile.init();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    AtomicLong getDataSize = new AtomicLong(0);

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
            e.printStackTrace();
            System.out.println("decode body error! discard this message!");
            in.readerIndex(oldReadIdx + bodyLength);
            return true;
        } finally {
            header = null;
        }
    }
    private boolean decodeBody0(ByteBuf in, List<Object> out) throws IOException, ClassNotFoundException {
        int bodyLength = Bytes.bytes2int(header, 11);
        long reqId = Bytes.bytes2long(header, 3);
        if(in.readableBytes() < bodyLength) {
            return false;
        }

        System.out.println(reqId + " decode start " + System.currentTimeMillis() + "---------------------get data size: " + getDataSize.addAndGet(bodyLength + 15));

        ByteBuf body = Unpooled.buffer(bodyLength);
        in.readBytes(body);

        int length = body.readInt();
        byte[] bytes = new byte[length];
        body.readBytes(bytes, 0, length);
        String clazz = new String(bytes, "UTF-8");
        length = body.readInt();
        bytes = new byte[length];
        body.readBytes(bytes, 0, length);
        String method = new String(bytes, "UTF-8");
        length = body.readInt();
        bytes = new byte[length];
        body.readBytes(bytes, 0, length);
        String desc = new String(bytes, "UTF-8");

        List<Object> argsList = new ArrayList<Object>();
        while (body.readableBytes() > 0) {
            length = body.readInt();
            ByteBuf temp = Unpooled.buffer(length);
            body.readBytes(temp);
            byte[] bytess = new byte[length];
            temp.getBytes(0, bytess);
            ByteArrayInputStream is = new ByteArrayInputStream(bytess);
            HessianInput hi = new HessianInput(is);
            argsList.add(hi.readObject());
        }
        Object[] args = argsList.toArray();

        String reqInfo = String.format(System.currentTimeMillis() + "      reqId=%s, clazz=%s, method=%s, desc=%s, args=%s", reqId, clazz, method, desc, args.toString());
        System.out.println("reqInfo : "+reqInfo);
        //reciFile.write("reci reqid = " + reqId + "\n");

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

        //System.out.println("get req id = " + reqId);

        int bodyLength = in.readInt();

        header = new byte[15];
        Bytes.short2bytes(magic, header);
        header[2] = flag;
        Bytes.long2bytes(reqId, header, 3);
        Bytes.int2bytes(bodyLength, header, 11);
        return true;
    }
}
