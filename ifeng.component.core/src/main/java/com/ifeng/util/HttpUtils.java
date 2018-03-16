package com.ifeng.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

/**
 * Created by gengyl on 2017/7/21.
 */
public class HttpUtils {

    /**
     * 获取客户端真实ip<br/>
     * 首先获取X-Forwarded-For第一个ip，如果不存在<br/>
     * 则获取X-Real-IP，如果不存在<br/>
     * 则通过ctx获取channel的remoteAddress方法获取
     * @param ctx
     * @param request
     * @return
     */
    public static String getRequestRealIp(ChannelHandlerContext ctx, HttpRequest request) {
        String ip = request.headers().get("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，用逗号分割，最左侧为真实ip
            int index = ip.indexOf(",");
            if(index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.headers().get("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }

        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return inetSocketAddress.getAddress().getHostAddress();
    }

    /**
     * 判断http请求是否keep alive
     * @param request
     * @return
     */
    public static boolean isKeepAlive(HttpRequest request) {
        CharSequence connection = request.headers().get(HttpHeaderNames.CONNECTION);
        if (connection != null && HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection)) {
            return false;
        }

        if (request.protocolVersion().isKeepAliveDefault()) {
            return !HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection);
        } else {
            return HttpHeaderValues.KEEP_ALIVE.contentEqualsIgnoreCase(connection);
        }
    }
}
