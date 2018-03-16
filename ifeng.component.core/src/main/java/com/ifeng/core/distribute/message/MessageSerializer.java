package com.ifeng.core.distribute.message;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class MessageSerializer<T> {
    public static <T> ByteBuf serialize(T t){
        try{
            Schema<T> schema = (Schema<T>) SchemaGenerator.getSchema(t.getClass());
            LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
            byte[] bs = ProtostuffIOUtil.toByteArray(t,schema,buffer);
            return Unpooled.copiedBuffer(bs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static <T> T deserialize(ByteBuf byteBuf,Class<T> clazz){
        T message = null;
        try{
            Schema<T> schema = SchemaGenerator.getSchema(clazz);
            LinkedBuffer buffer = LinkedBuffer.allocate(1024);
            message = clazz.newInstance();
            byte[] bs = ProtostuffIOUtil.toByteArray( message,schema,buffer);
            ProtostuffIOUtil.mergeFrom(bs,message,schema);
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    public static <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }

        Schema<T> schema = SchemaGenerator.getSchema(targetClass);
        List<T> result = null;
        try {
            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
        } catch (IOException e) {
            throw new RuntimeException("反序列化对象列表发生异常!",e);
        }
        return result;
    }

    public static <T> ByteBuf serializeList(List<T> list){
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("序列化对象列表(" + list + ")参数异常!");
        }
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) SchemaGenerator.getSchema(list.get(0).getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
        byte[] protostuff = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, list, schema, buffer);
            protostuff = bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("序列化对象列表(" + list + ")发生异常!", e);
        } finally {
            buffer.clear();
            try {
                if(bos!=null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Unpooled.copiedBuffer(protostuff);
    }
}
