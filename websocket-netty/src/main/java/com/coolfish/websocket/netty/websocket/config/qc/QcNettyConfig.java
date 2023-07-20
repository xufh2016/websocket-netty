package com.coolfish.websocket.netty.websocket.config.qc;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author xfh
 */
@Slf4j
public class QcNettyConfig {
    /**
     * 定义一个channel组，管理所有的channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup qcChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存放用户与Chanel的对应信息，用于给指定用户发送消息
     */
    private static ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> qcUserChannelMap = new ConcurrentHashMap<>();


    /**
     * 用来存放所有的channel channel.id().asShortText() --2 channels
     */
    private static ConcurrentHashMap<String, Channel> allChannels = new ConcurrentHashMap<>();


    private QcNettyConfig() {
    }

    public static ConcurrentHashMap<String, Channel> getAllChannels() {
        return allChannels;
    }

    /**
     * 获取channel组
     *
     * @return
     */
    public static ChannelGroup getChannelGroup() {
        return qcChannelGroup;
    }

    /**
     * 获取用户channel map
     *
     * @return
     */
    public static ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> getUserChannelMap() {
        return qcUserChannelMap;
    }


    /**
     * 添加channel
     * @param key 可以为空
     * @param channel
     */
    public static void addKeyChannel(String key, Channel channel) {
        getChannelGroup().add(channel);
        if (StrUtil.isNotBlank(key)) {
            ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> userChannelMap = getUserChannelMap();
            if (!userChannelMap.containsKey(key)) {
                CopyOnWriteArraySet<Channel> keyChannels = new CopyOnWriteArraySet<>();
                keyChannels.add(channel);
                userChannelMap.put(key, keyChannels);
                log.info("添加了1个连接----现在共有有{}个连接",keyChannels.size());
            } else {
                CopyOnWriteArraySet<Channel> hasKeyChannels = userChannelMap.get(key);
                log.info("添加连接----原来有{}个连接",hasKeyChannels.size());
                hasKeyChannels.add(channel);
                log.info("添加连接----添加完以后共有有{}个连接",hasKeyChannels.size());
            }
        }
        if (!allChannels.contains(channel)) {
            allChannels.put(channel.id().asShortText(), channel);
        }
        log.info("调用addKeyChannel方法，key = {}", key);
    }

    /**
     * 删除Channel
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        getChannelGroup().remove(channel);
        if (allChannels.containsKey(channel.id().asShortText())) {
            allChannels.remove(channel.id().asShortText(), channel);
        }
        ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> userChannelMap = getUserChannelMap();
        Set<Map.Entry<String, CopyOnWriteArraySet<Channel>>> entries = userChannelMap.entrySet();
        if (entries.size() > 0) {
            entries.forEach(kv -> {
                CopyOnWriteArraySet<Channel> channels = kv.getValue();
                if (channels.contains(channel)) {
                    channels.removeIf(channel1 -> channel1.equals(channel));
                }
            });
        }
    }

    /**
     * 根据key删除指定的某个channel
     * @param key
     * @param channel
     */
    public static void removeKeyChannel(String key, Channel channel) {
        getChannelGroup().remove(channel);
        CopyOnWriteArraySet<Channel> channelSet = qcUserChannelMap.get(key);
        log.info("即将移除一个连接，现在共有：{}",channelSet.size());
        if (channelSet.size() > 0 && channelSet.contains(channel)) {
            channelSet.remove(channel);
            if (allChannels.contains(channel)) {
                allChannels.remove(channel.id().asShortText(), channel);
            }
        }
        log.info("移除了一个连接，还剩：{}",channelSet.size());
    }

    /**
     * 根据channelId查找Channel
     *
     * @param channelShortId
     * @return CopyOnWriteArraySet<Channel>
     */
    public static Channel findChannel(String channelShortId) {
        ConcurrentHashMap<String, Channel> allChannels = getAllChannels();
        if (allChannels.containsKey(channelShortId)) {
            return allChannels.get(channelShortId);
        }
        return null;
    }

    /**
     * 根据key查找Channel
     *
     * @param key instModel:instId:username
     * @return
     */
    public static CopyOnWriteArraySet<Channel> findChannelsByKey(String key) {
        CopyOnWriteArraySet<Channel> channels = qcUserChannelMap.get(key);
        if (channels != null && channels.size() > 0) {
            return channels;
        }
        return null;
    }
}
