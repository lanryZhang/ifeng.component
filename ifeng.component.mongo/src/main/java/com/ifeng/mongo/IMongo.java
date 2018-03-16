package com.ifeng.mongo;

import com.ifeng.data.IDecode;
import com.ifeng.data.IEncode;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ifeng.mongo.query.Where;
import org.bson.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface IMongo {
//    Map<String, Object> selectAll(MongoSelect select) throws Exception;

    /**
	 * 根据condition的值，获取集合中符合条件的一条文档
	 * @param select 筛选条件
	 * @param classType 实体类型
	 * @return
	 * @throws Exception
	 */
    <T extends IDecode> T selectOne(MongoSelect select, Class<T> classType) throws Exception;

    BulkWriteResult batchUpdate(List<Map<String, Object>> fields, List<Where> wheres, boolean upset)  throws Exception;

	BulkWriteResult batchIncr(List<Map<String, Number>> fields, List<Where> wheres, boolean upset)  throws Exception;

	<T extends IEncode> BulkWriteResult batchIncr(List<T> en, List<Map<String, Number>> fields, List<Where> wheres, boolean upset)  throws Exception;

	<T extends IDecode> List<T> distinct(MongoSelect select, Class<T> classType);

	DistinctIterable<String> distinct(String distinctName, Document where) throws IllegalAccessException, InstantiationException;

    MongoCursor selectAllCursor(MongoSelect select) throws Exception;

    /**
	 * 获取集合内所有文档
	 * @param classType 实体类型
	 * @param select 
	 * @return
	 * @throws Exception
	 */
    <T extends IDecode> List<T> selectAll(MongoSelect select, Class<T> classType) throws Exception;
	
	/**
	 * 根据condition的值，获取集合中符合条件的所有文档
	 * @param select 筛选条件
	 * @param classType 实体类型
	 * @return
	 * @throws Exception
	 */
    <T extends IDecode> List<T> selectList(MongoSelect select, Class<T> classType) throws Exception;


    <T extends IDecode> List<T> selectListByAggregate(MongoSelect select, Class<T> classType) throws Exception;

    /**
	 * 删除符合条件的文档
	 * @param where 筛选条件
	 * @return
	 * @throws Exception
	 */
    DeleteResult remove(Where where) throws Exception;
	
	/**
	 * 保存一个文档到数据库
	 * @param en 实例
	 * @return
	 * @throws Exception
	 */
    <T  extends IEncode> void insert(T en)  throws Exception;
	
	/**
	 * 保存一组文档到数据库
	 * @param list 实例列表
	 * @return
	 * @throws Exception
	 */
    <T extends IEncode> void insert(List<T> list)  throws Exception;

	/**
	 * 完全更新符合条件的文档，只保留Map中的字段，其余字段删除
	 * @param fields
	 * @return
	 * @throws Exception
	 */
    UpdateResult update(Map<String, Object> fields, Where where) throws Exception;

	UpdateResult update(Map<String, Object> fields, Where where, Boolean upsert) throws Exception;

	<T extends IEncode> UpdateResult update(T en, Where where,boolean upsert,Date expire) throws Exception;

	<T extends IEncode> void insert(T en, Date expire) throws Exception;

	<T extends IEncode> void insert(List<T> list, Date expire) throws Exception;

	UpdateResult inc(Map<String, Number> fields, Where where) throws Exception;

	<T extends IEncode> UpdateResult inc(T en, Map<String, Number> incField, Where where, Boolean upsert) throws Exception;

	<T extends IEncode> UpdateResult inc(T en, Map<String, Number> incField,
										 Where where,
										 Boolean upsert,
										 Date expire) throws Exception;

	MongoCursor mapReduce(String map, String reduce, String outputTarget,
						  OutputType outputType, Where where) throws Exception;

	/**
	 *
	 * @return
	 * @throws Exception
	 */
    int count(MongoSelect select) throws Exception;

	/**
	 *
	 * @return
	 * @throws Exception
	 */
    int count() throws Exception;
	/**
	 * 关闭链接
	 * @throws Exception 
	 */
    void close() throws Exception;



}
