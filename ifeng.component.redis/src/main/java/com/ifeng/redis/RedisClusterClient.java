/*
* RedisClusterClient.java 
* Created on  202017/5/9 10:41 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.redis;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class RedisClusterClient extends AbsRedisClient {
    public RedisClusterClient(JedisCommands commands) {
        super(commands);
    }

    @Override
    protected JedisCommands getResource() throws Exception {
        return jd;
    }

    @Override
    public void close(JedisCommands jd) throws Exception {
    }
}
