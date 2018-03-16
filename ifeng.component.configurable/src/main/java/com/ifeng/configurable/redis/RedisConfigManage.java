/*
* RedisConfigManage.java 
* Created on  202016/11/1 17:56 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.configurable.redis;

import com.ifeng.configurable.ComponentConfiguration;
import com.ifeng.configurable.Context;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class RedisConfigManage  {

    private Map<String, RedisConfig> redisConfigs;
    private static final Logger logger = Logger.getLogger(RedisConfigManage.class);
    private String path;

    public RedisConfigManage(String path) {
        this.path = path;
    }

    public Map<String, RedisConfig> getRedisConfigs() {
        initRedis();
        return redisConfigs;
    }


    public void initRedis() {
        try {
            ComponentConfiguration comConfig = new ComponentConfiguration();
            HashMap<String,Context> map = comConfig.load(path);

            List<String> instances = comConfig.getAllInstances(); //pro.getProperty("redis.instances").split(",");

            if (redisConfigs == null) {
                redisConfigs = new HashMap<>();
            }

            for (String in : instances) {
                RedisConfig config = new RedisConfig();
                Context context = map.get(in);
                config.setDescription(context.getString( "description"));
                config.setMaxIdle(context.getInt( "maxIdle"));
                config.setMaxTotal(context.getInt("maxTotal"));
                config.setMaxWait(context.getInt("maxWait"));
                config.setMinEvictableIdleTimeMillis(context.getInt("minEvictableIdleTimeMillis"));
                config.setName(in);
                config.setPort(context.getInt("port"));
                config.setServerIp(context.getString("serverIp"));
                config.setShard(context.getString("sharded"));
                config.setNumTestsPerEvictionRun(context.getInt("numTestsPerEvictionRun"));
                config.setTestOnBorrow(context.getBoolean("testOnBorrow","true"));
                config.setTestOnReturn(context.getBoolean("testOnReturn","false"));
                config.setTestWhileIdle(context.getBoolean("testWhileIdle","true"));
                config.setTimeBetweenEvictionRunsMillis(context.getInt("timeBetweenEvictionRunsMillis"));
                redisConfigs.put(in, config);
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

}
