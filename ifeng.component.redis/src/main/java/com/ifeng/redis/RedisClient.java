package com.ifeng.redis;

import redis.clients.jedis.*;

import java.io.Closeable;

public class RedisClient extends AbsRedisClient{
    public RedisClient(String name, Boolean sharded) throws Exception {
        super(name,sharded);
    }

    public RedisClient(String name, Boolean sharded,String path) throws Exception {
        super(name,sharded,path);
    }

    public RedisClient(String name) throws Exception {
        super(name);
    }

    public RedisClient(String name,String path) throws Exception {
        super(name,path);
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
    }

    @Override
    protected JedisCommands getResource() throws Exception {
        JedisCommands jd = null;
        try {
            if ((jedisPool == null && shardedJedisPool == null) && (jd == null)) {
                return null;
            }
            if (useSharded) {
                jd = shardedJedisPool.getResource();
            } else if (jedisPool != null){
                jd = jedisPool.getResource();
                if (!((Jedis) jd).isConnected()) {
                    ((Jedis) jd).connect();
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return jd;
    }

    @Override
    public void close(JedisCommands jd) throws Exception {
        try {
            if ((shardedJedisPool != null || jedisPool != null) && jd != null) {
                ((Closeable)jd).close();
            }
        } catch (Exception e) {
            throw e;
        }
    }
}