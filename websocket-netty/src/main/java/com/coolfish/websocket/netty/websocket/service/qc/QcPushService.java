package com.coolfish.websocket.netty.websocket.service.qc;

import com.coolfish.websocket.netty.websocket.config.NettyConfig;
import com.coolfish.websocket.netty.websocket.config.qc.QcNettyConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author xfh
 */
@Slf4j
@Component
public class QcPushService {
    /**
     * 推送给指定用户
     *
     * @param channelId channelId as shortText
     * @param msg
     */
    public void pushMsgToOneByChannelId(String channelId, String msg) {
        Channel channel = QcNettyConfig.findChannel(channelId);

        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    /**
     * 推送给指定的所有通道
     *
     * @param key : instModel:instId+"客户端真实Ip地址"
     * @param msg
     */
    public void pushMsgToOneByKey(String key, String msg) {
        CopyOnWriteArraySet<Channel> channels = QcNettyConfig.findChannelsByKey(key);
        if (channels != null && channels.size() > 0) {
            channels.forEach(channel -> {
                if (channel != null) {
                    log.info("定点向key：{}，推送了数据：{}", key, msg);
                    channel.writeAndFlush(new TextWebSocketFrame(msg));
                }
            });

        } else {
            throw new RuntimeException("通道已被断开");
        }
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
