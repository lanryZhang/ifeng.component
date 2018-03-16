/*
* HttpObjectAggregatorEx.java 
* Created on  202017/2/20 16:14 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.handlers.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import sun.misc.Launcher;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class HttpObjectAggregatorEx extends HttpObjectAggregator {
    private static final String TOO_LOARGE_MEESAGE = "{msg:\"message too large.\",success:false}";
//    private static final FullHttpResponse TOO_LARGE = new DefaultFullHttpResponse(
//            HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpObjectAggregatorEx.class);

    public HttpObjectAggregatorEx(int maxContentLength) {
        super(maxContentLength);
    }

    public HttpObjectAggregatorEx(int maxContentLength, boolean closeOnExpectationFailed) {
        super(maxContentLength, closeOnExpectationFailed);
    }

    @Override
    protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
//        super.handleOversizedMessage(ctx, oversized);

        if (oversized instanceof HttpRequest) {
            // send back a 413 and close the connection
            ByteBuf byteBuf = Unpooled.copiedBuffer(TOO_LOARGE_MEESAGE, CharsetUtil.UTF_8);
            FullHttpResponse tooLarge = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,HttpResponseStatus.OK, byteBuf);
            ChannelFuture future = ctx.writeAndFlush(tooLarge.retainedDuplicate()).addListener(
                    (ChannelFutureListener) future1 -> {
                        if (!future1.isSuccess()) {
                            logger.debug("Failed to send a 413 Request Entity Too Large.", future1.cause());
                        }
                        ctx.close();
                    });

            // If the client started to send data already, close because it's impossible to recover.
            // If keep-alive is off and 'Expect: 100-continue' is missing, no need to leave the connection open.
            if (oversized instanceof FullHttpMessage ||
                    !HttpUtil.is100ContinueExpected(oversized) && !HttpUtil.isKeepAlive(oversized)) {
                future.addListener(ChannelFutureListener.CLOSE);
            }

            // If an oversized request was handled properly and the connection is still alive
            // (i.e. rejected 100-continue). the decoder should prepare to handle a new message.
            HttpObjectDecoder decoder = ctx.pipeline().get(HttpObjectDecoder.class);
            if (decoder != null) {
                decoder.reset();
            }
        } else if (oversized instanceof HttpResponse) {
            ctx.close();
            throw new TooLongFrameException("Response entity too large: " + oversized);
        } else {
            throw new IllegalStateException();
        }
    }
}
