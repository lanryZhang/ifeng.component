/*
* KafkaRecordDispatcher.java 
* Created on  202016/12/12 15:58 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core;

import com.ifeng.compress.IUncompress;
import com.ifeng.core.distribute.handlers.http.HandlerMapper;
import com.ifeng.core.serialization.Deserializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public abstract class AbsDispatcher implements Dispatcher{
    protected HandlerMapper mapper ;
    protected Deserializable deserializable;
    protected IUncompress uncompress;
    protected Map<String, String> asyncHandleThreadNumMap = new HashMap<>();

    public void setDeserializable(Deserializable deserializable) {
        this.deserializable = deserializable;
    }

    public void setUncompress(IUncompress uncompress) {
        this.uncompress = uncompress;
    }

    public void put(String key, MessageProcessor processor){
        mapper.registHandler(key ,processor);
    }

    public void setMapper(HandlerMapper mapper) {
        this.mapper = mapper;
    }

    public void putConsumerAsyncHandleThreadNum(String key, String tNum) { this.asyncHandleThreadNumMap.put(key, tNum); }
    public String getConsumerAsyncHandleThreadNum(String key) { return this.asyncHandleThreadNumMap.get(key); }
}
