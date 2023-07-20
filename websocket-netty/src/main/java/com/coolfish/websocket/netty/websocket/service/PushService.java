package com.coolfish.websocket.netty.websocket.service;

import com.coolfish.websocket.netty.websocket.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xfh
 */
@Slf4j
@Component
public class PushService {
    /**
     * 推送给指定用户
     *
     * @param userId
     * @param msg
     */
    public void pushMsgToOne(String userId, String msg) {
        ConcurrentHashMap<String, Channel> userChannelMap = NettyConfig.getUserChannelMap();
        Channel channel = userChannelMap.get(userId);
        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    /**
     * 推送给所有用户
     *
     * @param msg
     */
    public void pushMsgToAll(String msg) {
        log.info("----------------------websocket推送的消息是：{}", msg);
        NettyConfig.getChannelGroup().writeAndFlush(new TextWebSocketFrame(msg));
    }
}
