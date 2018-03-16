package com.ifeng.core.distribute.filters;

import com.ifeng.configurable.Context;

import java.util.Collection;

/**
 * Created by gengyl on 2017/7/19.
 */
public interface IFilter {
    boolean filter(Context context);
    void updateRule(Context context);
}
