package com.loggle.rpc.sea.remoting.api;

/**
 * @author guomy
 * @create 2016-08-05 17:47.
 */
public class Invocation {
    private String clazz;
    private String method;
    private Class<?>[] paramTypes;
    private Object[] args;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
