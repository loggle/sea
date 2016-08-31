package com.loggle.rpc.common.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author guomy
 * @create 2016-08-30 15:20.
 */
public class Pool<T> extends BasePooledObjectFactory<T> {


    @Override
    public T create() throws Exception {


        return null;
    }

    @Override
    public PooledObject<T> wrap(T t) {
        return new DefaultPooledObject<T>(t);
    }
}
