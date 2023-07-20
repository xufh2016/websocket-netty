package com.coolfish.websocket.netty.websocket.util;

import cn.hutool.core.util.StrUtil;

/**
 * @className: WsChannelKeyUtil
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2023/6/5
 */
public class WsChannelKeyUtil {
    /**
     * 生成ws中通道的key，ws通道key是这样定义的 instModel:instId:username
     *
     * @param instModel 仪器类型
     * @param instId    仪器编号
     * @param username  用户名
     * @return
     */
    public static String generateWsChannelKey(String instModel, String instId, String username) {
        if (StrUtil.isNotBlank(instModel) && StrUtil.isNotBlank(instId) && StrUtil.isNotBlank(username)) {
            StringBuffer sb = new StringBuffer();
            sb.append(instModel).append(":").append(instId).append(":").append(username);
            return sb.toString();
        } else {
            throw new RuntimeException("入参有空，不能生成key");
        }
    }
}
