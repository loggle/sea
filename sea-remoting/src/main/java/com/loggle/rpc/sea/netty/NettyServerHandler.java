package com.loggle.rpc.sea.netty;

import com.loggle.rpc.sea.api.Invocation;
import com.loggle.rpc.sea.api.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.lang.reflect.Method;

/**
 * Created by my on 2016/8/21.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {

        Invocation invocation = (Invocation) request.getData();
        String clazz = invocation.getClazz();
        String method = invocation.getMethod();
        Class[] type = invocation.getParamTypes();
        Object[] args = invocation.getArgs();
        Class<?> aClass = Class.forName(clazz);
        try {
            Object o = aClass.newInstance();
            Method method1 = o.getClass().getMethod(method, type);
            String rest = (String)method1.invoke(o, args);
            ctx.channel().writeAndFlush(rest);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(request);
        }
    }
}
