package com.ifeng.core.distribute.codec;

import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.MessageSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class MessageEncode extends MessageToByteEncoder{
//    @Override
//    protected void encode(ChannelHandlerContext channelHandlerContext, BaseMessage baseMessage, List<Object> list) throws Exception {
//        if (baseMessage == null){
//            return;
//        }
//        ByteBuf byteBuf = MessageSerializer.serialize(baseMessage);
//
//        list.add(byteBuf);
//    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ByteBuf byteBuf = null;
        try {
            byteBuf = MessageSerializer.serialize(msg);
            out.writeInt(byteBuf.readableBytes());
            out.writeBytes(byteBuf);
        }catch (Exception er){
            er.printStackTrace();
        }finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }
}
