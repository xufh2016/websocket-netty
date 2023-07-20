package com.coolfish.websocket.netty.websocket.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @className: ExceptionHandler
 * @description: 异常处理handler
 * @author: xufh
 * @date: 2022/9/27
 */
@Component
@Slf4j
@ChannelHandler.Sharable
@Scope("prototype")
public class ExceptionHandler extends ChannelDuplexHandler {
    /**
     * 读数据
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause != null) {
            log.error("读数据异常：{}", cause.getMessage());
            ctx.close();
        }
    }

    /**
     * 写数据
     *
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise.addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                log.error("写数据异常：{}", channelFuture.cause());
                ctx.close();
            }
        }));
    }

}
