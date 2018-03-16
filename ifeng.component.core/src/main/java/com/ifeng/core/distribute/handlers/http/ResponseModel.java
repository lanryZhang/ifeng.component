/*
* ResponseModel.java 
* Created on  202016/12/22 17:16 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.handlers.http;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ResponseModel {
    private HttpResponseStatus status;
    private Object content;
    private Map<String, Object> headers = new HashMap<>();

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeader(String headerName, Object headerValue) {
        headers.put(headerName, headerValue);
    }
}
