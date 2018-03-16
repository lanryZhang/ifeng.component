package com.ifeng.core.distribute.message;

import java.util.Map;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class Header {
    /**
     * 报文长度（header+body）
     */
    private int length = 4;
    /**
     * 报文类型
     * 0	业务请求消息
     * 1	业务响应消息
     * 2	业务ONE WAY消息 （既是请求消息也是响应消息）
     * 3	握手请求消息
     * 4	握手响应消息
     * 5	心跳请求消息
     * 6	心跳应答消息
     */
    private byte type;


    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
