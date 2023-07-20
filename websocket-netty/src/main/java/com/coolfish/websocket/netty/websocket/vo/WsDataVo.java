package com.coolfish.websocket.netty.websocket.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: WsDataVo
 * @description: websocket推送数据实体类
 * @author: xufh
 * @date: 2023/5/30
 */
@Data
@Builder
public class WsDataVo<T> implements Serializable {

    /**
     * 发送的数据类型
     */
    private String dataType;

    /**
     * 具体的数据
     */
    private T data;
}
