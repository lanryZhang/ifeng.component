/*
* RequestHandler.java 
* Created on  202016/12/14 15:14 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.handlers.http;

import com.alibaba.fastjson.JSONObject;
import com.ifeng.configurable.Configurable;
import com.ifeng.configurable.Context;
import com.ifeng.core.Dispatcher;
import com.ifeng.core.distribute.constances.ContextConstances;
import com.ifeng.util.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> implements Configurable {


    private Dispatcher dispatcher;
    private final static Logger logger = Logger.getLogger(HttpRequestHandler.class);

    //TODO delete this
    private ThreadLocal<Long> costThreadLocal = new ThreadLocal<>();

    public void messageReceived(ChannelHandlerContext ctx, HttpObject message) throws Exception {
        try {

            //TODO delete this
            costThreadLocal.set(System.currentTimeMillis());

            if (message instanceof HttpRequest) {
                Context context = new Context();
                HttpRequest request = (HttpRequest) message;
                context.put("request", request);
                context.put("client_ip", HttpUtils.getRequestRealIp(ctx, request));

                logger.debug("httpMethod is " + request.method().toString());

                if (request.method() == HttpMethod.GET || request.method() == HttpMethod.HEAD) {

                    parsePostParameters(request.uri(), context);
                    ResponseModel res = (ResponseModel) dispatcher.dispatch(context);

                    writeResponse(ctx.channel(), res, request);

                } else if (request.method() == HttpMethod.POST) {

                    HttpContent chunk = (HttpContent) message;

                    if (chunk instanceof LastHttpContent) {
                        ByteBuf buf = chunk.content();
                        String data = buf.toString(CharsetUtil.UTF_8);

                        buf.clear();

                        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
                        if ((null != contentType) && (contentType.toLowerCase().startsWith("application/json"))) {
                            Object object = JSONObject.parse(data);

                            if (object != null) {
                                context.put("data", object);
                            }
                            parsePostParameters(request.uri(), context);
                        } else {
                            String uri = request.uri();
                            if (null != data && !"".equals(data)) {
                                if (uri.contains("?")) {
                                    uri = uri + "&" + data;
                                } else {
                                    uri = uri + "?" + data;
                                }
                            }
                            parsePostParameters(uri, context);
                        }

                        ResponseModel res = (ResponseModel) dispatcher.dispatch(context);

                        writeResponse(ctx.channel(), res, request);

                    }
                } else {
                    parsePostParameters(request.uri(), context);
                    ResponseModel res = (ResponseModel) dispatcher.dispatch(context);

                    writeResponse(ctx.channel(), res, request);
                }
            }
        } catch (Exception e) {
            //TODO delete this
            writeLog(message, e);
            throw e;
        }
    }

    private void parsePostParameters(String uri, Context context){
        QueryStringDecoder decoderQuery = new QueryStringDecoder(uri);

        HttpRequest request = (HttpRequest) context.getObject("request");
        String key = StringUtils.strip(decoderQuery.path()+"/$" + request.method(),"/");
        context.put(ContextConstances.PROCESSOR_KEY,key);

        Map<String, List<String>> uriAttributes = decoderQuery.parameters();
        for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
            for (String attrVal : attr.getValue()) {
                context.put(attr.getKey(),attrVal);
            }
        }
    }
    /**
     * http返回响应数据
     *
     * @param channel
     */
    private void writeResponse(Channel channel, ResponseModel model, HttpRequest request) throws Exception {
        try {
            // Convert the response content to a ChannelBuffer.
            ByteBuf buf = copiedBuffer(model.getContent().toString(), CharsetUtil.UTF_8);

            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, model.getStatus(), buf);
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            //add additional headers
            Map<String, Object> headers = model.getHeaders();
            if (null != headers && !headers.isEmpty()) {
                Iterator<Map.Entry<String, Object>> iterator = headers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> en = iterator.next();
                    response.headers().set(en.getKey(), en.getValue());
                }
            }

            // Write the response.

            /**
            // Decide whether to close the connection or not.
            //boolean isKeepAlive = HttpUtils.isKeepAlive(request);

            // Close the connection after the write operation is done if necessary.
            if (isKeepAlive) {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                channel.writeAndFlush(response);
            } else {
                channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }*/
            response.headers().set(CONNECTION, HttpHeaderValues.CLOSE);
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                    //TODO delete this
                    .addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) {
                            Long beginMillis = costThreadLocal.get();
                            if (null != beginMillis){
                                logger.debug("future result is " + future.isSuccess() + ",request cost " + (System.currentTimeMillis()-beginMillis) +" millis");
                            }
                        }
                    });

            buf.clear();
        } catch (Exception e) {
            throw  e;
        }
    }

    //TODO delete this
    public void writeLog(HttpObject message, Exception e) {
        try {
            HttpRequest request = (HttpRequest) message;
            String uri = request.uri();

            StringBuffer errInfo = new StringBuffer("writeResponse throw Exception");
            errInfo.append(",[uri=").append(uri).append("]")
                    .append(",[method=").append(request.method()).append("]");

            QueryStringDecoder decoderQuery = new QueryStringDecoder(uri);
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {
                    errInfo.append(",[").append(attr.getKey()).append("=").append(attrVal).append("]");
                }
            }

            HttpHeaders headers = request.headers();
            List<Map.Entry<String, String>> headerList = headers.entries();
            for (int i = 0; i < headerList.size(); i++) {
                Map.Entry header = headerList.get(i);
                errInfo.append(",[").append(header.getKey()).append("=").append(header.getValue()).append("]");
            }
            logger.error(errInfo.toString(), e);
        } catch (Exception innerEx) {
            logger.error("write log inner Exception", innerEx);
        }

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
            throws Exception {
        messageReceived(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().flush();
//        ctx.channel().close();
//        ctx.channel().disconnect();
        ctx.flush();
//        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //ctx.channel().close();

        if (ctx.channel().isOpen()){
            //TODO delete this
            ChannelHandler handler = ctx.handler();
            String errMsg = cause.getMessage();
            if (null != handler) {
                errMsg += ",ChannelHandler is " + handler.toString();
            }
//            logger.error(errMsg, cause);
            ctx.close();


//            logger.error(cause.getMessage(), cause);
//            ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
           ctx.close();
        }
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void config(Context context) {

    }


}
