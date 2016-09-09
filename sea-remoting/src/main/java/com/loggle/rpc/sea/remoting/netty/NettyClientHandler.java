package com.loggle.rpc.sea.remoting.netty;

import com.loggle.rpc.common.fileio.WriteToFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.util.concurrent.Future;

/**
 * Created by my on 2016/8/21.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

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
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        String[] strs = msg.split("\\|");
        long reqId = Long.parseLong(strs[0]);
        NettyClient.CallBack callBack = (NettyClient.CallBack)NettyClient.getFuture(reqId);
        callBack.done(strs[1]);
        //resultFile.write(msg + "\n");
        System.out.println("from server : " + msg);
    }
}
