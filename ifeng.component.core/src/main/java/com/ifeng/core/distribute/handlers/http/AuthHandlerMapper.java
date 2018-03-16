package com.ifeng.core.distribute.handlers.http;

import com.ifeng.core.distribute.handlers.IHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gengyl on 2017/7/19.
 */
public class AuthHandlerMapper {
    private Map<String,IHandler> mapper = new ConcurrentHashMap<>();

    public void registHandler(String key, IHandler handler){
        this.mapper.put(key,handler);
    }
    public IHandler get(String key){
        return mapper.get(key);
    }
}
