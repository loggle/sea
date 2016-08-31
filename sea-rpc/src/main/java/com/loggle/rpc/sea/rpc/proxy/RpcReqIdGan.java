package com.loggle.rpc.sea.rpc.proxy;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author guomy
 * @create 2016-08-30 11:20.
 */
public class RpcReqIdGan {

    static AtomicLong atomicLong = new AtomicLong(1);

    public static long getReqId() {
        return atomicLong.getAndIncrement();
    }
}
