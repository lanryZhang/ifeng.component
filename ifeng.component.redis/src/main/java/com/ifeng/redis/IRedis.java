package com.ifeng.redis;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.Tuple;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRedis {
    List<String> getList(String key, int pageSize, int pageIndex) throws Exception;

    <T> List<T> getList(String key, int pageSize, int pageIndex, Class<T> clazz) throws Exception;

    /**
     * 获取list对象
     *
     * @param key
     * @return
     * @throws Exception
     */
    <T> List<T> getList(String key, Class<T> clazz) throws Exception;

    long hincr(String key, String key2, long value) throws Exception;

    long hincr(String key, String key2) throws Exception;

    long hdecr(String key, String key2, long value) throws Exception;

    long hdecr(String key, String key2) throws Exception;

    long incr(String key) throws Exception;

    long incr(String key, int value) throws Exception;

    long decr(String key) throws Exception;

    long decr(String key, int value) throws Exception;

    long lrem(String key, String value) throws Exception;

    String lindex(String key, long index) throws Exception;

    long llen(String key) throws Exception;

    <T> long lrem(String key, T value) throws Exception;

    /**
     * 获取存储对象
     *
     * @param key
     * @return
     * @throws Exception
     */
    <T> T get(String key, Class<T> clazz) throws Exception;

    Map<String, String> bacthGet(List<String> keys) throws Exception;

    <T> Map<String, T> bacthGet(List<String> keys, Class<T> clazz) throws Exception;

    /**
     * 获取存储的值 --字符串类型
     *
     * @param key
     * @return
     * @throws Exception
     */
    String getString(String key) throws Exception;

    /**
     * 获取List<String>
     *
     * @param key
     * @return
     * @throws Exception
     */
    List<String> getStringList(String key) throws Exception;

    /**
     * 存储list对象
     *
     * @param key
     * @param list
     * @throws Exception
     */
    <T> void set(String key, List<T> list) throws Exception;

    /**
     * 存储list对象，并且设置过期时间
     *
     * @param key
     * @param list
     * @param expire 秒
     * @throws Exception
     */
    <T> void set(String key, List<T> list, int expire) throws Exception;

    /**
     * 存储单个实例对象
     *
     * @param key
     * @param t
     * @return
     * @throws Exception
     */
    <T> String set(String key, T t) throws Exception;

    /**
     * 存储单个实例对象，并且设置过期时间
     *
     * @param key
     * @param t
     * @param expire 秒
     * @return
     * @throws Exception
     */
    <T> String set(String key, T t, int expire) throws Exception;

    /**
     * 存储string类型的值
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    String setString(String key, String value) throws Exception;

    /**
     * 存储string类型的值，并且设置过期时间
     *
     * @param key
     * @param value
     * @param expire 秒
     * @return
     * @throws Exception
     */
    String setString(String key, String value, int expire) throws Exception;

    /**
     * 存储List<String>对象
     *
     * @param key
     * @param list
     * @throws Exception
     */
    void setListString(String key, List<String> list) throws Exception;

    /**
     * 存储List<String>对象,并且设置过期时间
     *
     * @param key
     * @param list
     * @param expire 秒
     * @throws Exception
     */
    void setListString(String key, List<String> list, int expire) throws Exception;

    /**
     * List中插入一条数据
     *
     * @param key
     * @param t
     * @throws Exception
     */
    <T> void lpush(String key, T t) throws Exception;

    abstract String hmset(String key, Map<String, String> map) throws Exception;

    <T> String hset(String key, Map<String, T> map) throws Exception;

    <T> String hset(String key, String key2, T en) throws Exception;

    Long hset(String key, String key2, String value) throws Exception;

    long hmdel(String key, String... keys) throws Exception;

    <T> List hmget(String key, Class<T> clazz, String... keys) throws Exception;

    String hget(String key, String key2) throws Exception;

    Map<String, String> hgetAll(String key) throws Exception;

    Map<String, Map<String, String>> hgetAll(List<String> keys) throws Exception;

    <T> Map<String, T> hgetAll(String key, Class<T> clazz) throws Exception;

    <T> T hmget(String key, Class<T> clazz, String key2) throws Exception;

    /**
     * 弹出一条数据
     *
     * @param key
     * @return
     * @throws Exception
     */
    <T> T lpop(String key, Class<T> clazz) throws Exception;


    /**
     * 将list修剪到指定范围内的元素
     *
     * @param key
     * @param start 开始下标，从0开始计数，0表示list第一个元素
     * @param end   结束下标，从0开始计数，0表示list第一个元素
     */
    void ltrim(String key, long start, long end) throws Exception;

    /**
     * List中插入多条string类型数据
     *
     * @param key
     * @param values
     * @throws Exception
     */
    void lpushString(String key, String... values) throws Exception;

    /**
     * list截取
     *
     * @param key
     * @param start
     * @param end
     * @param <T>
     * @return
     */
    <T> List<T> lrange(String key, long start, long end, Class<T> clazz) throws Exception;

    /**
     * List中弹出一条string类型数据
     *
     * @param key
     * @return
     * @throws Exception
     */
    String lpopString(String key) throws Exception;

    String rpopString(String key) throws Exception;

    /**
     * 删除一个String类型的键值对
     *
     * @param key
     * @return
     * @throws Exception
     */
    long del(String key) throws Exception;

    /**
     * 判断是否存在一个String类型的key
     *
     * @param key
     * @return
     * @throws Exception
     */
    boolean existsString(String key) throws Exception;

    /**
     * 设置缓存过期时间
     *
     * @param key
     * @param expire
     * @throws Exception
     */
    void expireKey(String key, int expire) throws Exception;


    void close(JedisCommands jd) throws Exception;

    void rpushString(String key, String value) throws Exception;

    List<String> hmget(String key, String... keys) throws Exception;

    Long zadd(String key, double score, String member) throws Exception;

    <T> Long zadd(String key, double score, T member) throws Exception;

    <T> Long zadd(String key, Map<T, Double> scoreMembers) throws Exception;

    Double zscore(String key, String member) throws Exception;

    Long zrem(String key, String... member) throws Exception;

    <T> Long zrem(String key, List<T> list) throws Exception;

    Double zincrby(String key, double score, String member) throws Exception;

    <T> Double zincrby(String key, double score, T member) throws Exception;

    Set<String> zrange(String key, long start, long end) throws Exception;

    Set<Tuple> zrevrangeWithScores(String key, long start, long end) throws Exception;

    Set<Tuple> zrangeWithScores(String key, long start, long end) throws Exception;

    Set<String> zrangeByScores(String key, double start, double end) throws Exception;

    Long zcount(String key, double min, double max) throws Exception;

    Long zcard(String key) throws Exception;

    <T> Set<T> zrange(String key, long start, long end, Class<T> clazz) throws Exception;

    Set<String> zrevrange(String key, long start, long end) throws Exception;

    <T> Set<T> zrevrange(String key, long start, long end, Class<T> clazz) throws Exception;

    Set<String> smembers(String key) throws Exception;

    <T> Set<T> smembers(String key, Class<T> clazz) throws Exception;

    Long saddString(String key, String... value) throws Exception;

    <T> Long sadd(String key, T value) throws Exception;

    Long sremString(String key, String... value) throws Exception;

    <T> Long srem(String key, T value) throws Exception;

    /**
     * set中是否存在该string
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    Boolean sismember(String key, String value) throws Exception;

    void lock(String key, long sleepTime) throws Exception;

    void unlock(String key) throws Exception;
}
