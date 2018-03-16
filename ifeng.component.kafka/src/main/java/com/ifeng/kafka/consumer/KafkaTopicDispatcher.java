/*
* KafkaRecordDispatcher.java 
* Created on  202016/12/12 16:04 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.consumer;

import com.ifeng.configurable.Context;
import com.ifeng.core.AbsDispatcher;
import com.ifeng.core.MessageProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class KafkaTopicDispatcher extends AbsDispatcher {

    @Override
    public Object dispatch(Context context) {
        ConsumerRecord record = (ConsumerRecord) context.getObject("data");

        MessageProcessor processor = mapper.get(record.topic());

        if (null == record || null == processor) {
            throw new NullPointerException("record can not be null and processor's size can not be nulls.");
        }
        if (null != deserializable) {
            String res = deserializable.deserialize(record.value()).toString();
            if (null != uncompress){
                try {
                    res = uncompress.uncompress(res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            context.put("data", res);
        }

        context.put("initPoolSize", getConsumerAsyncHandleThreadNum(record.topic()));
        return processor.process(context);

    }
}