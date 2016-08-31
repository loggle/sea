package com.loggle.rpc.sea.remoting.api;

/**
 * @author guomy
 * @create 2016-08-05 16:57.
 */
public class Response {
    private long id;
    private Object data;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
