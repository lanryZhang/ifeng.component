/*
* AbsRedisClient.java 
* Created on  202017/5/9 10:38 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.redis;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public abstract class AbsRedisClient implements IRedis {
    protected String redisName;
    protected JedisCommands jd;
    protected static JedisPool jedisPool;
    protected static ShardedJedisPool shardedJedisPool;
    protected boolean useSharded = false;
    protected String shardedName;
    protected String path;

    protected static final Logger logger = Logger.getLogger(RedisClient.class);

    public AbsRedisClient(JedisCommands commands) {
        this.jd = commands;
    }


    public AbsRedisClient(String name) throws Exception {
        this.redisName = name;
        initJedis();
    }

    public AbsRedisClient(String name,String path) throws Exception {
        this.redisName = name;
        this.path = path;
        initJedis();
    }

    public AbsRedisClient(String name, Boolean sharded) throws Exception {
        this.useSharded = sharded;
        if (useSharded) {
            this.shardedName = name;
            initShardedJedis();
        } else {
            this.redisName = name;
            initJedis();
        }
    }

    public AbsRedisClient(String name, Boolean sharded,String path) throws Exception {
        this.path = path;
        this.useSharded = sharded;
        if (useSharded) {
            this.shardedName = name;
            initShardedJedis();
        } else {
            this.redisName = name;
            initJedis();
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void initJedis() throws Exception {
        if (null == jedisPool) {
            synchronized (RedisClient.class) {
                if (null == jedisPool) {
                    jedisPool = new Redis(path).getJedisPool(redisName);
                }
            }
        }
    }

    public void initShardedJedis() throws Exception {
        if (null == shardedJedisPool) {
            synchronized (RedisClient.class) {
                if (null == shardedJedisPool) {
                    shardedJedisPool = new Redis(path).getShardedJedisPool(shardedName);
                }
            }
        }
    }

    protected abstract JedisCommands getResource() throws Exception;

    @Override
    public List<String> getList(String key, int pageSize, int pageIndex) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.lrange(key, (pageIndex - 1) * pageSize, pageIndex * pageSize - 1);

        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> List<T> getList(String key, int pageSize, int pageIndex, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            List<String> list = jedis.lrange(key, (pageIndex - 1) * pageSize, pageIndex * pageSize - 1);
            if (list == null) {
                return null;
            }
            List<T> res = new ArrayList<>();
            T t;
            for (String bs : list) {
                t = SerializeUtil.toObject(bs, clazz);
                res.add(t);
            }
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            List<String> list = jedis.lrange(key, 0, -1);
            if (list == null) {
                return null;
            }
            List<T> res = new ArrayList<>();
            T t;
            for (String bs : list) {
                t = SerializeUtil.toObject(bs, clazz);
                res.add(t);
            }
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long hincr(String key, String key2, long value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hincrBy(key, key2, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long hincr(String key, String key2) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hincrBy(key, key2, 1);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long hdecr(String key, String key2, long value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hincrBy(key, key2, (-value));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long hdecr(String key, String key2) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hincrBy(key, key2, -1);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long incr(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return incr(key, 1);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long incr(String key, int value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.incrBy(key, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long decr(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.decr(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long decr(String key, int value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.decrBy(key, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long lrem(String key, String value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.lrem(key, 1, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }


    @Override
    public String lindex(String key, long index) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.lindex(key, index);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long llen(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.llen(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> long lrem(String key, T value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String res = SerializeUtil.toJsonString(value);
            return jedis.lrem(key, 1, res);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String en = jedis.get(key);
            // 反序列化
            return SerializeUtil.toObject(en, clazz);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Map<String, String> bacthGet(List<String> keys) throws Exception {
        JedisCommands jedis = null;
        jedis = getResource();
        ShardedJedisPipeline shardedJedisPipeline = null;
        Pipeline pipeline = null;
        if (useSharded) {
            shardedJedisPipeline = ((ShardedJedis) jedis).pipelined();
        } else {
            pipeline = ((Jedis) jedis).pipelined();
        }
        try {
            Map<String, Response<String>> pipmap = new ConcurrentHashMap<>();
            Map<String, String> resMap = new ConcurrentHashMap<>();
            for (String key : keys) {
                pipmap.put(key, (null == pipeline ? shardedJedisPipeline : pipeline).get(key));
            }
            if (useSharded) {
                shardedJedisPipeline.sync();
            } else {
                pipeline.sync();
            }
            for (Map.Entry<String, Response<String>> en : pipmap.entrySet()) {
                resMap.put(en.getKey(), en.getValue().get());
            }

            return resMap;

        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
            if (null != pipeline) {
                pipeline.clear();
                pipeline.close();
            }
        }
    }

    @Override
    public <T> Map<String, T> bacthGet(List<String> keys, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        jedis = getResource();

        ShardedJedisPipeline shardedJedisPipeline = null;
        Pipeline pipeline = null;
        if (useSharded) {
            shardedJedisPipeline = ((ShardedJedis) jedis).pipelined();
        } else {
            pipeline = ((Jedis) jedis).pipelined();
        }

        try {
            Map<String, Response<String>> pipmap = new ConcurrentHashMap<>();
            Map<String, T> resMap = new ConcurrentHashMap<>();
            for (String key : keys) {
                pipmap.put(key, (null == pipeline ? shardedJedisPipeline : pipeline).get(key));
            }
            if (useSharded) {
                shardedJedisPipeline.sync();
            } else {
                pipeline.sync();
            }
            for (Map.Entry<String, Response<String>> en : pipmap.entrySet()) {
                resMap.put(en.getKey(), SerializeUtil.toObject(en.getValue().get(), clazz));
            }
            return resMap;

        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            if (null != pipeline) {
                pipeline.clear();
                pipeline.close();
            }
            close(jedis);
        }
    }

    @Override
    public String getString(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public List<String> getStringList(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }

    }

    @Override
    public void setListString(String key, List<String> list) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.del(key);
            if (list.size() > 0) {
                jedis.lpush(key, list.toArray(new String[list.size()]));
            }
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }

    }

    @Override
    public <T> String set(String key, T t) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.set(key, SerializeUtil.toJsonString(t));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> void set(String key, List<T> list) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.del(key);
            if (null != list && list.size() > 0) {
                String[] res = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    res[i] = SerializeUtil.toJsonString(list.get(i));
                }
                jedis.lpush(key, res);
            }

//            jedis.lpush(key, SerializeUtil.toJsonString(list.toArray(new String[list.size()])));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public String setString(String key, String value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.set(key, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }


    @Override
    public <T> void set(String key, List<T> list, int expire) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.del(key);
            jedis.lpush(key, SerializeUtil.toJsonString(list.toArray(new String[list.size()])));
            jedis.expire(key, expire);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> String set(String key, T t, int expire) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String values = SerializeUtil.toJsonString(t);
            String res = jedis.set(key, values);
            jedis.expire(key, expire);
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public String setString(String key, String value, int expire) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String res = jedis.set(key, value);
            jedis.expire(key, expire);
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public void setListString(String key, List<String> list, int expire) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.del(key);
            jedis.lpush(key, list.toArray(new String[list.size()]));
            jedis.expire(key, expire);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public void expireKey(String key, int expire) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.expire(key, expire);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public abstract void close(JedisCommands jd) throws Exception;

    @Override
    public <T> void lpush(String key, T t) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.lpush(key, SerializeUtil.toJsonString(t));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }


    @Override
    public void lpushString(String key, String... values) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.lpush(key, values);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> List<T> lrange(String key, long start, long end, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            List<String> list = jedis.lrange(key, start, end);
            if (list == null) {
                return null;
            }
            List<T> res = new ArrayList<>();
            T t;
            for (String bs : list) {
                t = SerializeUtil.toObject(bs, clazz);
                res.add(t);
            }
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public void ltrim(String key, long start, long end) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.ltrim(key, start, end);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public void rpushString(String key, String value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            jedis.rpush(key, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public List<String> hmget(String key, String... keys) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hmget(key, keys);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public String hmset(String key, Map<String, String> map) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hmset(key, map);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> String hset(String key, Map<String, T> map) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Map<String, String> stringMap = new HashMap<>();
            for (Map.Entry<String, T> entry : map.entrySet()) {
                stringMap.put(entry.getKey(), SerializeUtil.toJsonString(entry.getValue()));
            }
            return jedis.hmset(key, stringMap);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> String hset(String key, String key2, T en) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put(key2, SerializeUtil.toJsonString(en));
            return jedis.hmset(key, stringMap);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Long hset(String key, String key2, String value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hset(key, key2, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public long hmdel(String key, String... keys) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hdel(key, keys);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> List hmget(String key, Class<T> clazz, String... keys) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            List<String> stringList = jedis.hmget(key, keys);
            if (stringList == null) {
                return null;
            }
            List<T> res = new ArrayList<>();
            T t;
            for (String bs : stringList) {
                t = SerializeUtil.toObject(bs, clazz);
                res.add(t);
            }
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Map<String, String> hgetAll(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Map<String, Map<String, String>> hgetAll(List<String> keys) throws Exception {
        return null;
    }

    @Override
    public <T> Map<String, T> hgetAll(String key, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Map<String, T> res = new TreeMap<>();
            Map<String, String> t = jedis.hgetAll(key);
            for (Map.Entry<String, String> en : t.entrySet()) {
                res.put(en.getKey(), SerializeUtil.toObject(en.getValue(), clazz));
            }
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public String hget(String key, String key2) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String s = jedis.hget(key, key2);
            if (s == null) {
                return null;
            }
            return s;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> T hmget(String key, Class<T> clazz, String key2) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String s = jedis.hget(key, key2);
            if (s == null) {
                return null;
            }
            T t = SerializeUtil.toObject(s, clazz);
            return t;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> T lpop(String key, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String res = jedis.lpop(key);
            return SerializeUtil.toObject(res, clazz);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public String lpopString(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String res = jedis.lpop(key);
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }

    }

    @Override
    public String rpopString(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String res = jedis.rpop(key);
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }

    }

    @Override
    public long del(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.del(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }

    }

    @Override
    public boolean existsString(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }

    }

    @Override
    public Long zadd(String key, double score, String member) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Long zadd(String key, double score, T member) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String res = SerializeUtil.toJsonString(member);
            return jedis.zadd(key, score, res);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Long zadd(String key, Map<T, Double> scoreMembers) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Map<String, Double> res = new HashMap<>();
            for (Map.Entry<T, Double> en : scoreMembers.entrySet()) {
                if (en.getKey() instanceof String) {
                    return jedis.zadd(key, (Map<String, Double>) scoreMembers);
                }
                res.put(SerializeUtil.toJsonString(en.getKey()), en.getValue());
            }
            return jedis.zadd(key, res);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Double zscore(String key, String member) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Long zrem(String key, String... member) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();

            return jedis.zrem(key, member);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Long zrem(String key, List<T> list) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (list != null) {
                String[] res = new String[list.size()];

                list.toArray(res);
                return jedis.zrem(key, res);
            }
            return Long.valueOf(-1);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Double zincrby(String key, double score, String member) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zincrby(key, score, member);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Double zincrby(String key, double score, T member) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zincrby(key, score, SerializeUtil.toJsonString(member));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Set<String> zrange(String key, long start, long end) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long end) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeWithScores(key, start, end);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Set<String> zrangeByScores(String key, double start, double end) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zrangeByScore(key, start, end);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Long zcount(String key, double min, double max) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Long zcard(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Set<T> zrange(String key, long start, long end, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Set<String> t = jedis.zrange(key, start, end);
            Set<T> res = new HashSet<T>();
            Iterator<String> ite = t.iterator();
            while (ite.hasNext()) {
                res.add(SerializeUtil.toObject(ite.next(), clazz));
            }
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Set<String> zrevrange(String key, long start, long end) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Set<T> zrevrange(String key, long start, long end, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Set<String> t = jedis.zrevrange(key, start, end);
            Set<T> res = new HashSet<T>();
            Iterator<String> ite = t.iterator();
            while (ite.hasNext()) {
                res.add(SerializeUtil.toObject(ite.next(), clazz));
            }
            return res;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Set<String> smembers(String key) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Set<T> smembers(String key, Class<T> clazz) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Set<String> members = jedis.smembers(key);
            if (members == null || members.isEmpty()) {
                return Collections.emptySet();
            }
            Set<T> resultSet = Collections.emptySet();
            Iterator<String> iterator = members.iterator();
            while (iterator.hasNext()) {
                String memStr = iterator.next();
                T member = SerializeUtil.toObject(memStr, clazz);
                resultSet.add(member);
            }
            return resultSet;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Long saddString(String key, String... value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Long sadd(String key, T value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.sadd(key, SerializeUtil.toJsonString(value));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Long sremString(String key, String... value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public <T> Long srem(String key, T value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.srem(key, SerializeUtil.toJsonString(value));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    /**
     * set中是否存在该string
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public Boolean sismember(String key, String value) throws Exception {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public void lock(String key, long sleepTime) throws Exception {
        JedisCommands jedis = null;
        try {
            String value = Thread.currentThread().getName();
            jedis = getResource();
            //防止程序崩溃导致的死锁
            jedis.expire(key, 100);
            while (jedis.setnx(key, value) == 0) {
                Thread.sleep(sleepTime);
            }
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public void unlock(String key) throws Exception {
        del(key);
    }
}
