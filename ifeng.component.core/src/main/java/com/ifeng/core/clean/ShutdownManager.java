/*
* ShutdownManager.java 
* Created on  202017/6/5 13:55 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.clean;

import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ShutdownManager extends Thread {

    private static Vector<CleanupAware> threads = new Vector<>();
    private static Logger logger = Logger.getLogger(ShutdownManager.class);

    public static boolean regist(CleanupAware thread){
        return threads.add(thread);
    }

    @Override
    public void run() {
        for (CleanupAware r:threads) {
            try {
                r.cleanup();
                Thread.sleep(10);
            }catch (Exception er){
                logger.error("shutting down thread --" + r+"error:"+er);
            }
        }
    }
}
