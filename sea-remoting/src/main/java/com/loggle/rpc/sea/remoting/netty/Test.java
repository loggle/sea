package com.loggle.rpc.sea.remoting.netty;

/**
 * @author guomy
 * @create 2016-08-31 17:56.
 */
public class Test {
    public static void main(String[] args) {
        int i = 0;
        int j = 15;
        for(int k=0; k<100; k++) {
            System.out.println(k & (j-1));
        }
    }
}
