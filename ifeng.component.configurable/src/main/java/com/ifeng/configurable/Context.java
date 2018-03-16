package com.ifeng.configurable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanglr on 2016/6/27.
 */
public class Context {
    private HashMap<String,Object> context;

    public Context(){
        context = new HashMap<String,Object>();
    }
    public void put(String key,Object value){
         context.put(key,value);
    }
    public void putAll(Map<String,Object> m){
        context.putAll(m);
    }
    public void putAll(Context c){
        context.putAll(c.getContext());
    }
    private Object get(String key) {
        return this.get(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        Object value = this.get(key);
        return value != null?Long.valueOf(Long.parseLong(value.toString().trim())):defaultValue;
    }

    public Integer getInt(String key, Integer defaultValue) {
        Object value = this.get(key,defaultValue.toString());
        return value != null?Integer.valueOf(Integer.parseInt(value.toString().trim())):defaultValue;
    }

    public Integer getInt(String key) {
        return this.getInt(key, 0);
    }
    public Long getLong(String key) {
        return this.getLong(key, 0L);
    }
    public String getString(String key, String defaultValue) {
        return this.get(key, defaultValue).toString();
    }
    public Boolean getBoolean(String key ){return (Boolean) this.get(key, false);}
    public Boolean getBoolean(String key,String defaultValue){return Boolean.valueOf(this.get(key, defaultValue).toString());}
    public Boolean getBoolean(String key,Boolean defaultValue){return (Boolean) this.get(key, defaultValue);}
    public String getString(String key) {
        return this.get(key,"").toString();
    }
    public Object getObject(String key){
        return context.get(key);
    }
    private Object get(String key, Object defaultValue) {
        Object result = this.context.get(key);
        return result != null?result:defaultValue;
    }

    public Map<String,Object> getContext(){
        return this.context;
    }
}
