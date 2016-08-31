package com.loggle.rpc.sea.remoting.netty;

import com.loggle.rpc.common.fileio.WriteToFile;
import com.loggle.rpc.sea.remoting.api.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;

/**
 * Created by my on 2016/8/21.
 */
public class NettyClientHandler2 extends SimpleChannelInboundHandler<Response> {

    private static WriteToFile resultFile;

    static {
        resultFile = new WriteToFile("e:/test/result.log");
        try {
            resultFile.init();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {

        long reqId = response.getId();
        NettyClient.CallBack callBack = (NettyClient.CallBack)NettyClient.getFuture(reqId);
        callBack.done((String) response.getData());
        resultFile.write((String) response.getData() + "\n");
        System.out.println("from server : " + reqId + "|" + response.getData());
    }
}
