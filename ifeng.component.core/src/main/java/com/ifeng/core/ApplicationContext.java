/*
* ApplicationContext.java 
* Created on  202016/12/15 13:58 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core;

import com.ifeng.core.distribute.handlers.http.AuthFilterMapper;
import com.ifeng.core.distribute.handlers.http.AuthHandlerMapper;
import com.ifeng.core.distribute.handlers.http.HandlerMapper;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ApplicationContext {
    public ApplicationContext(){
        this.mapper = new HandlerMapper();
        this.authHandlerMapper = new AuthHandlerMapper();
        this.authFilterMapper = new AuthFilterMapper();
    }

    private HandlerMapper mapper;
    public HandlerMapper getMapper() {
        return mapper;
    }
    public void setMapper(HandlerMapper mapper) {
        this.mapper = mapper;
    }

    private AuthHandlerMapper authHandlerMapper;
    public AuthHandlerMapper getAuthHandlerMapper() {
        return authHandlerMapper;
    }
    public void setAuthHandlerMapper(AuthHandlerMapper authHandlerMapper) {
        this.authHandlerMapper = authHandlerMapper;
    }

    private AuthFilterMapper authFilterMapper;
    public AuthFilterMapper getAuthFilterMapper() {
        return authFilterMapper;
    }
    public void setAuthFilterMapper(AuthFilterMapper authFilterMapper) {
        this.authFilterMapper = authFilterMapper;
    }


}
