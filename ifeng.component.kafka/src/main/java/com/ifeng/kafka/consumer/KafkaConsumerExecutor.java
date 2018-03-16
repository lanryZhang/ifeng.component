/*
* KafkaConsumerExecutor.java 
* Created on  202016/12/13 14:34 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.consumer;

import com.ifeng.compress.ICompress;
import com.ifeng.compress.IUncompress;
import com.ifeng.configurable.Configurable;
import com.ifeng.configurable.Context;
import com.ifeng.core.AbsDispatcher;
import com.ifeng.core.clean.CleanupAware;
import com.ifeng.core.clean.KillSignalHandler;
import com.ifeng.core.distribute.handlers.http.HandlerMapper;
import com.ifeng.core.serialization.Deserializable;
import com.ifeng.kafka.constances.KafkaConstances;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import static com.ifeng.kafka.constances.KafkaConstances.MESSAGE_COMPRESS;
import static com.ifeng.kafka.constances.KafkaConstances.MESSAGE_UNCOMPRESS;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class KafkaConsumerExecutor implements Configurable,CleanupAware ,Runnable{
    private int threadNum;
    private boolean isSync;
    private int asyncHandleThreadNum;
    private Collection<String> topics;
    private ExecutorService executorService;
    private Context conf;
    private AbsDispatcher dispatcher;
    private Map<Future,KafkaConsumer> resultMap = new ConcurrentHashMap<>();
    private Set<Future> errorSet = new HashSet<>();
    private static final Logger logger = Logger.getLogger(KafkaConsumerExecutor.class);
    private CountDownLatch shutdown = new CountDownLatch(1);


    private void execute() throws Exception{

        if (dispatcher == null){
            throw new Exception("消息处理器不能为空.");
        }

        for (int i = 0; i < threadNum; i++) {
            if (isSync) {
                newSyncTask();
            } else {
                newAsyncTask();
            }
        }

        checkHealthy();
    }

    private void newSyncTask(){
        SyncKafkaConsumerProxy proxy = new SyncKafkaConsumerProxy(conf);
        proxy.setDispatcher(dispatcher);
        Future future = executorService.submit(proxy);
        resultMap.put(future,proxy);
    }

    private void newAsyncTask(){
        AsyncKafkaConsumerProxy proxy = new AsyncKafkaConsumerProxy(conf);
        proxy.setDispatcher(dispatcher);
        Future future = executorService.submit(proxy);
        resultMap.put(future,proxy);
    }


    private void checkHealthy(){
        while (shutdown.getCount() > 0){
            logger.info("while: shutdown.getCount() " + shutdown.getCount());
            resultMap.forEach((k,v)->{
                try {
                    CountDownLatch countDownLatch = (CountDownLatch) k.get(3000, TimeUnit.MILLISECONDS);
                    if (countDownLatch.getCount() == 0) {
                        errorSet.add(k);
                    }
                }catch (TimeoutException e){

                } catch (InterruptedException e) {
                    logger.error(e);
                } catch (ExecutionException e) {
                    logger.error(e);
                }
            });

            if (errorSet.size() > 0) {
                errorSet.forEach(r -> {
                    resultMap.remove(r);
                    r.cancel(true);

                    if (shutdown.getCount() > 0) {
                        synchronized (shutdown){
                            if (shutdown.getCount() > 0){
                                if (isSync){
                                    newSyncTask();
                                }else{
                                    newAsyncTask();
                                }
                            }
                        }
                    }

                });

                errorSet.clear();
            }else{
                try {
                    Thread.currentThread().sleep(10 * 1000);
                } catch (InterruptedException e) {
                    logger.error(e);
                    break;
                }
            }
        }
    }

    public void shutdown(){
        synchronized (shutdown) {
            shutdown.countDown();
            logger.info("shutdown " + shutdown.getCount());
        }
        resultMap.forEach((k,v)->{
            v.wakeup();
            if (!k.isCancelled()) {
                k.cancel(true);
            }
        });
        if (!executorService.isShutdown()){
            executorService.shutdown();
        }
    }

    public void setDispatcher(AbsDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    @Override
    public void config(Context context) {
        this.conf = context;
        this.isSync = context.getBoolean("isSync", "true");
        this.threadNum = context.getInt("threadNum", 10);
        this.asyncHandleThreadNum = context.getInt("asyncHandleThreadNum", 10);
        String topicStr = context.getString("topic.name", "");
        topics = Arrays.asList(topicStr.split(","));
        this.conf.put("topics",topics);
        executorService = Executors.newFixedThreadPool(threadNum);
        String disp = context.getString("dispatcher");
        try {
            if (null != disp && !disp.isEmpty()) {
                dispatcher = (AbsDispatcher) Class.forName(disp).newInstance();
            }
            dispatcher.setMapper((HandlerMapper) context.getObject("handlerMapper"));
            String deserializer = context.getString("dispatcher.value.deserializer", "");
            if (!deserializer.isEmpty()) {
                Deserializable deserializable = (Deserializable)Class.forName(deserializer).newInstance();
                dispatcher.setDeserializable(deserializable);
            }

            String str = context.getString(MESSAGE_UNCOMPRESS,"");
            if (!str.isEmpty()) {
                IUncompress uncompress = (IUncompress) Class.forName(str).newInstance();
                dispatcher.setUncompress(uncompress);
            }

            //put kafka consumer async handle thread num from config
            topics.forEach(topic -> {
                dispatcher.putConsumerAsyncHandleThreadNum(topic, String.valueOf(this.asyncHandleThreadNum));
            });
        } catch (ClassNotFoundException e) {
            logger.error(e);
        } catch (InstantiationException e) {
            logger.error(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
        }

        KillSignalHandler.regist(this);
    }

    @Override
    public void cleanup() {
        logger.info(Thread.currentThread().getName() + "---Consumer shutdown now. ");
        shutdown();
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
