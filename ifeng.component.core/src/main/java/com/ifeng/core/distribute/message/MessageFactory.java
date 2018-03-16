package com.ifeng.core.distribute.message;

import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class MessageFactory {
    /**
     * 创建握手请求消息
     *
     * @return
     */
    public static BaseMessage createLoginReqMessage(Object body) {
        BaseMessage loginMessage = buildMessage(body);
        loginMessage.getHeader().setType(MessageType.AUTH_REQ);
        return loginMessage;
    }

    /**
     * 创建握手响应消息
     *
     * @return
     */
    public static BaseMessage createLoginResMessage(Object body) {
        BaseMessage loginMessage = buildMessage(body);
        loginMessage.getHeader().setType(MessageType.AUTH_RESP);
        return loginMessage;
    }

    /**
     * 创建心跳请求消息
     *
     * @return
     */
    public static BaseMessage createHeartBeatReqMessage() {
        BaseMessage heartbeatMessage = buildMessage(null);
        heartbeatMessage.getHeader().setType(MessageType.HEART_REQ);
        return heartbeatMessage;
    }

    /**
     * 创建心跳响应消息
     *
     * @return
     */
    public static BaseMessage createHeartBeatRespMessage() {
        BaseMessage heartbeatMessage = buildMessage(null);
        heartbeatMessage.getHeader().setType(MessageType.HEART_RESP);
        return heartbeatMessage;
    }

    /**
     * 创建监控收集请求
     * @return
     */
    public static BaseMessage createMonitorReqMessage(){
        BaseMessage monitorReqMessage = buildMessage(null);
        monitorReqMessage.getHeader().setType(MessageType.MONITOR_REQ);
        return monitorReqMessage;
    }

    /**
     * 创建监控应答消息
     * @param obj SpiderJobDescriptor
     * @return
     */
    public static BaseMessage createMonitorRespMessage(Object obj){
        BaseMessage monitorReqMessage = buildMessage(obj);
        monitorReqMessage.getHeader().setType(MessageType.MONITOR_RESP);
        return monitorReqMessage;
    }

    public static BaseMessage createTaskAssignmentReqMessage(Object obj){
        BaseMessage message = buildMessage(obj);
        message.getHeader().setType(MessageType.TASK_ASSIGN_REQ);
        return message;
    }
    public static BaseMessage createTaskAssignmentRespMessage(Object obj){
        BaseMessage message = buildMessage(obj);
        message.getHeader().setType(MessageType.TASK_ASSIGN_RESP);
        return message;
    }

    private static BaseMessage buildMessage(Object body) {
        BaseMessage message = new BaseMessage();
        Header header = new Header();
        if (body == null){
            header.setLength(0);
        }else{
            ByteBuf by ;

            by = MessageSerializer.serialize(body);

            header.setLength(by.readableBytes());
        }
        header.setType(MessageType.AUTH_REQ);
        message.setHeader(header);
        message.setBody(body);
        return message;
    }
}
