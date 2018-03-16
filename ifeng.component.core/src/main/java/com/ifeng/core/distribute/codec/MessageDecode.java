package com.ifeng.core.distribute.codec;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.ifeng.core.distribute.message.BaseMessage;
import com.ifeng.core.distribute.message.SchemaGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class MessageDecode extends ByteToMessageDecoder {
    private final static Logger logger = Logger.getLogger(MessageDecode.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int frameLen = in.markReaderIndex().readInt();
        if (in.readableBytes() < frameLen){
            in.resetReaderIndex();
            return;
        }
        ByteBuf byteBuf = in.readSlice(frameLen).retain();
        byte[] bs;
        if (byteBuf.hasArray()){
            bs = byteBuf.array();
        }else{
            int length = byteBuf.readableBytes();//2
            bs = new byte[length];    //3
            byteBuf.getBytes(byteBuf.readerIndex(), bs);
        }

        Schema<BaseMessage> schema = SchemaGenerator.getSchema(BaseMessage.class);
        BaseMessage baseMessage = new BaseMessage();
        try {
            ProtostuffIOUtil.mergeFrom(bs, baseMessage, schema);
        }catch (Exception er){
            logger.error(er);
        }finally {
            if (in != null){
                in.clear();
            }
            if (byteBuf != null) {
                byteBuf.release();
            }
        }

        out.add(baseMessage);
    }
}
