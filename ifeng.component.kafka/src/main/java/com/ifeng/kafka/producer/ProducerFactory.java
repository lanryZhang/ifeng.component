/*
* Producer.java 
* Created on  202016/11/4 17:48 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.producer;

import com.ifeng.configurable.ComponentConfiguration;
import com.ifeng.configurable.Context;
import com.ifeng.configurable.kafka.KafkaConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ProducerFactory {
    public static String BASE_DIR = "";
    public static void setBaseDir(String path){
        BASE_DIR = path;
    }
    private static ThreadLocal<KafkaProducerEx> kafkaProducerExThreadLocal  = new ThreadLocal<>();
    private static ConcurrentHashMap<String,KafkaProducerEx> kafkaProducerExMap  = new ConcurrentHashMap<String,KafkaProducerEx>();

    public static KafkaProducerEx getInstnace(String topic){
        try {
            if (null == kafkaProducerExThreadLocal.get()) {
                synchronized (ProducerFactory.class) {
                    if (null == kafkaProducerExThreadLocal.get()) {
                        String kafkaPro = BASE_DIR +"/" + "kafka.properties";
                        String topicPro = BASE_DIR +"/" + "kafkatopics.properties";

                        if (null == ComponentConfiguration.getMap().get(kafkaPro)){
                            ComponentConfiguration baseConf = new ComponentConfiguration();
                            baseConf.load(kafkaPro);
                            baseConf.load(topicPro);
                        }

                        Map<String,Context> conf = ComponentConfiguration.getMap().get(kafkaPro);
                        Map<String,Context> tm = ComponentConfiguration.getMap().get(topicPro);
                        conf.putAll(tm);
                        Context context = conf.get("kafkaProducer");
                        Context topicContext= tm.get(topic);
                        if (null != topicContext) {
                            context.putAll(tm.get(topic).getContext());
                        }
                        kafkaProducerExThreadLocal.set(new KafkaProducerEx(context.getContext(),topic));
                    }
                }
            }
            return kafkaProducerExThreadLocal.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static KafkaProducerEx getInstnace(){
        try {
            if (null == kafkaProducerExThreadLocal.get()) {
                synchronized (ProducerFactory.class) {
                    if (null == kafkaProducerExThreadLocal.get()) {
                        String kafkaPro = BASE_DIR +"/" + "kafka.properties";
                        ComponentConfiguration baseConf = new ComponentConfiguration();
                        baseConf.load(kafkaPro);
                        Map<String,Context> conf = ComponentConfiguration.getMap().get(kafkaPro);
                        kafkaProducerExThreadLocal.set(new KafkaProducerEx(conf.get("kafkaProducer").getContext()));
                    }
                }
            }
            return kafkaProducerExThreadLocal.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static KafkaProducerEx getInstnaces(String topicName){
        try {
            if (null == kafkaProducerExMap.get(topicName)) {
                synchronized (ProducerFactory.class) {
                    if (null == kafkaProducerExMap.get(topicName)) {
                        String kafkaPro = BASE_DIR +"/" + "kafka.properties";
                        ComponentConfiguration baseConf = new ComponentConfiguration();
                        baseConf.load(kafkaPro);
                        Map<String,Context> conf = ComponentConfiguration.getMap().get(kafkaPro);
                        kafkaProducerExMap.put(topicName,(new KafkaProducerEx(conf.get("kafkaProducer").getContext(),topicName)));
                    }
                }
            }
            return kafkaProducerExMap.get(topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}