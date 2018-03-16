package com.ifeng.core.distribute.handlers;

import com.ifeng.configurable.Context;
import com.ifeng.core.distribute.filters.IFilter;

import java.util.Collection;
import java.util.Map;

/**
 * Created by gengyl on 2017/7/19.
 */
public interface IHandler {
    Object handle(Context context);
    void setSuit(Context suit);
}
