package com.loggle.rpc.sea.rpc.proxy;

import com.loggle.rpc.common.fileio.WriteToFile;
import com.loggle.rpc.sea.remoting.api.Client;
import com.loggle.rpc.sea.remoting.api.Invocation;
import com.loggle.rpc.sea.remoting.api.Request;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author guomy
 * @create 2016-08-30 11:09.
 */
public class RpcProxy implements InvocationHandler {

    private Client client;
    private Class clazz;
    private static WriteToFile sendFile;


    static {
        sendFile = new WriteToFile("e:/test/send.log");
        try {
            sendFile.init();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public RpcProxy(Client client, Class clazz) {
        this.client = client;
        this.clazz =  clazz;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation();
        invocation.setClazz(clazz.getName());

        Class[] type = {String.class};
        invocation.setParamTypes(type);
        invocation.setMethod(method.getName());

        invocation.setArgs(args);

        long reqId = RpcReqIdGan.getReqId();
        Request request = new Request();
        request.setId(reqId);
        request.setData(invocation);

        String msg = (String) args[0];

        System.out.println("send data :" + reqId + "|" + msg);
        sendFile.write(reqId + "|" + msg + "\n");

        Object result = client.sendAndWait(request);
        return result;
    }
}
