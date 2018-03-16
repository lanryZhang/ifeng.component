package com.ifeng.core.distribute.handlers.http;

import com.ifeng.core.distribute.filters.IFilter;
import com.ifeng.core.distribute.handlers.IHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gengyl on 2017/7/19.
 */
public class AuthFilterMapper {
    private Map<String,IFilter> mapper = new ConcurrentHashMap<>();
    private Map<String, String> ruleKeyMapper = new ConcurrentHashMap<>();

    public void registFilter(String key, IFilter filter, String ruleKey){
        this.mapper.put(key,filter);
        this.ruleKeyMapper.put(key, ruleKey);
    }
    public IFilter get(String key){
        return mapper.get(key);
    }
    public String getRuleKey(String key) {
        return ruleKeyMapper.get(key);
    }
    public Map<String, IFilter> getMapper() {
        return mapper;
    }
}
