package com.ifeng.core.distribute.handlers;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageFactory;
import com.ifeng.core.distribute.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class HeartBeatReqHanlder extends SimpleChannelInboundHandler<BaseMessage> {
	private static final Logger logger = Logger.getLogger(HeartBeatReqHanlder.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {

		if (msg.getHeader() != null && msg.getHeader().getType() == MessageType.HEART_RESP) {
			logger.info("Get heart beat response");
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		ctx.fireExceptionCaught(cause);
	}
}
