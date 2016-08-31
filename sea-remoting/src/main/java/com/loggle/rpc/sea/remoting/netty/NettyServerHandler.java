package com.loggle.rpc.sea.remoting.netty;

import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;
import com.loggle.rpc.sea.remoting.api.Response;
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
        long reqId = request.getId();
        Invocation invocation = (Invocation) request.getData();
        String clazz = invocation.getClazz();
        String method = invocation.getMethod();
        Class[] type = invocation.getParamTypes();
        Object[] args = invocation.getArgs();

        String implName = clazz.substring(0, clazz.lastIndexOf(".") + 1) + clazz.substring(clazz.lastIndexOf(".")).substring(2) + "Impl";
        Class<?> aClass = Class.forName(implName);


        try {
            Object o = aClass.newInstance();
            Method method1 = o.getClass().getMethod(method, type);
            String rest = (String)method1.invoke(o, args);
            Response response = new Response();
            response.setId(reqId);
            response.setData(rest);
            ctx.channel().writeAndFlush(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(request);
        }
    }
}
