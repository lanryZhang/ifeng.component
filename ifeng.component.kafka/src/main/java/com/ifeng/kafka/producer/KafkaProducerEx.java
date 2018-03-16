/*
* ProducerEx.java 
* Created on  202016/11/30 9:48 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.producer;


import com.ifeng.compress.ICompress;
import com.ifeng.core.clean.CleanupAware;
import com.ifeng.core.clean.KillSignalHandler;
import com.ifeng.kafka.exceptions.EmptyWorkThreadException;
import io.netty.buffer.ByteBuf;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.ifeng.kafka.constances.KafkaConstances.*;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class KafkaProducerEx extends KafkaProducer implements CleanupAware {
    private int batchSize = 1000;
    private int timeout = 3000;
    private long lastFlushTime = -1;
    private String topicName;
    private List<Future<RecordMetadata>> futures = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(KafkaProducerEx.class);
    private ConcurrentLinkedDeque<ProducerRecord> queue = new ConcurrentLinkedDeque<>();
    private AtomicInteger aliveWorker = new AtomicInteger(0);
    private CountDownLatch sendWorkCountDownLatch = new CountDownLatch(1);
    private ExecutorService executorService;
    private long maxBlockingSize = 200000L;
    private long sendThreshold = maxBlockingSize / 2;
    private AtomicLong queueSize = new AtomicLong(0);
    private ICompress compress;

    public KafkaProducerEx(Map<String, Object> configs) {
        this(configs,"");
    }

    public KafkaProducerEx(Map<String, Object> configs, String topicName) {
        super(configs);
        batchSize = configs.get(BATCH_SIZE) == null ? batchSize : Integer.valueOf(configs.get(BATCH_SIZE).toString());
        timeout = configs.get(FLUSH_TIMEOUT) == null ? timeout : Integer.valueOf(configs.get(FLUSH_TIMEOUT).toString());
        maxBlockingSize = configs.get(MAX_BLOCKING_SIZE) == null ? maxBlockingSize : Long.valueOf(configs.get(MAX_BLOCKING_SIZE).toString());
        executorService = Executors.newFixedThreadPool(20);
        this.topicName = topicName;

        if (configs.containsKey(MESSAGE_COMPRESS)){
            try{
                String str = configs.get(MESSAGE_COMPRESS).toString();
                compress = (ICompress) Class.forName(str).newInstance();
            }catch (Exception e){
               logger.error(e);
            }
        }
        start();
    }

    private boolean timeout() {
        return System.currentTimeMillis() - lastFlushTime > timeout;
    }

    public void start() {
        executorService.submit(new SendRecord());
        aliveWorker.incrementAndGet();
        KillSignalHandler.regist(this);
    }

    @Override
    public void cleanup() {
        logger.info(Thread.currentThread().getName() + "---ProducerEx shutdown now. ");
        sendWorkCountDownLatch.countDown();
        flush();
        if (!executorService.isShutdown()){
            executorService.shutdown();
        }
        super.close();
    }

    class SendRecord implements Runnable {
        @Override
        public void run() {
            try {
                while (sendWorkCountDownLatch.getCount() > 0) {
                    sendRecord();
                }

            } finally {
                aliveWorker.decrementAndGet();
            }
        }
    }

    @Override
    public void close(){
        cleanup();
    }
    @Override
    public void flush(){
        while(queue.size() > 0){
            sendRecord();
        }
    }

    private void sendDirect(String record) throws Exception{
        ProducerRecord producerRecord = new ProducerRecord<>(topicName, record);
        Future<RecordMetadata> future = null;
        try {
            future = send(producerRecord, new ProducerCallback());
        } catch (Exception ex) {
            future = send(producerRecord, new ProducerCallback());
            try {
                throw new Exception("send error");
            } catch (Exception e) {
                logger.error(e);
            }
        }
        future.get();
    }

    public void sendOneRecord(String record) throws Exception {
        sendDirect(record);
    }

    private void sendRecord() {
        ProducerRecord record = null;
        try {
            for (int processedEvents = 0; processedEvents < batchSize; processedEvents += 1) {

                record = queue.peekFirst();
                if (record == null) {
                    logger.info("record is null,topic --" + topicName);
                    Thread.currentThread().sleep(3000);
                    break;
                }else {
                    queueSize.decrementAndGet();
                    try {
                        futures.add(send(record, new ProducerCallback()));
                    } catch (Exception ex) {
                        try {
                            futures.add(send(record, new ProducerCallback()));
                        } catch (Exception e) {
                            throw new Exception("send error");
                        }
                        logger.error(ex);
                    }finally {
                        queue.pollFirst();
                    }
                }
            }
            if (futures.size() > 0) {
                super.flush();
                logger.info("flush logs " + futures.size()+", queue size:" + queue.size());
                try {
                    futures.forEach(k -> {
                        try {
                            k.get();
                        } catch (InterruptedException e) {
                            logger.error(e);
                        } catch (ExecutionException e) {
                            logger.error(e);
                        }
                    });
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    futures.clear();
                }
            } else {
                logger.info("flush logs 0");
                Thread.currentThread().sleep(1000);
            }
        } catch (Exception e) {
//            sendWorkCountDownLatch.countDown();
            logger.error(e);
        }
    }

    public int getAliveWorkCount() {
        return aliveWorker.get();
    }

    public void send(String record,Integer partition) {
        if (null == topicName) {
            throw new NullPointerException("topic name can not be null.");
        }
        if (null == record) {
            return;
        }

        try {
            if (queueSize.get() >= maxBlockingSize) {
                logger.error("queueSize:" + queueSize.get());
                Thread.currentThread().sleep(1);
            }
            if (null != compress) {
                record = compress.compress(record);
            }
            if (partition >= 0) {
                if (queue.offer(new ProducerRecord<>(topicName, partition, null, record))) {
                    queueSize.incrementAndGet();
                }else{
                  logger.error("offer message error:"+toString()+"----"+record);
                }
            } else {
                if (queue.offer(new ProducerRecord<>(topicName, record))) {
                    queueSize.incrementAndGet();
                }else{
                    logger.error("offer message error:"+toString()+"----"+record);
                }
            }

        } catch (InterruptedException er) {
            logger.error(er);
        } catch (IOException e) {
            logger.error(e);
        }
    }
    public void send(String record) {
        send(record,-1);
    }
}

class ProducerCallback implements Callback {
    private static final Logger logger = Logger.getLogger(ProducerCallback.class);

    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            logger.error("Error sending message to Kafka {} ", exception);
        }
    }
}
