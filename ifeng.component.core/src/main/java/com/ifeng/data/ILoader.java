package com.ifeng.data;

import java.util.Date;

/**
 * Created by zhanglr on 2016/10/9.
 */
public interface ILoader {
    int getInt(String key, int defaultValue);
    int getInt(String key);
    String getString(String key);
    Long getLong(String key);
    ILoader getLoader(String key) throws Exception;
    Date getDate(String key);
    Object getObject(String key);
}
