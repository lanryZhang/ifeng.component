/*
* BaseMessage.java 
* Created on  202017/6/2 19:59 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.message;
/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class BaseMessage {
    private Header header;
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}