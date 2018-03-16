package com.ifeng.kafka.consumer;


import com.ifeng.configurable.Context;
import com.ifeng.core.MessageProcessor;
import com.ifeng.core.clean.CleanupAware;
import com.ifeng.core.clean.KillSignalHandler;

import java.util.concurrent.*;


/**
 * Created by gengyl on 2017/7/4.
 */
public abstract class KafkaBaseController implements CleanupAware, MessageProcessor {
    private volatile ExecutorService executorService;
    private boolean shutdown = false;
    protected BlockingQueue<Context> taskQueue = new LinkedBlockingQueue<>();

    protected abstract Object doProcess(Context ctx);

    protected Object initThreadPool(Context context){

        if (executorService == null && !shutdown) {
            synchronized (this) {
                if (executorService == null && !shutdown){
                    int poolSize = context.getInt("initPoolSize", 10);
                    executorService = Executors.newFixedThreadPool(poolSize);

                    for (int i = 0 ; i < poolSize; i++){
                        executorService.submit(new TaskHandler());
                    }

                    KillSignalHandler.regist(this);
                }
            }
        }
        return null;
    }

    @Override
    public void cleanup() {
        synchronized (this) {
            this.shutdown = true;
            if(null != executorService && !executorService.isShutdown()){
                executorService.shutdown();
            }
        }
    }

    class TaskHandler implements Callable{
        public TaskHandler(){}

        @Override
        public Object call() throws Exception {
            try {
                while (!shutdown || !taskQueue.isEmpty()){
                    Context ctx = taskQueue.poll(5000, TimeUnit.MILLISECONDS);

                    if(null != ctx) {
                        doProcess(ctx);
                    } else {
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
