package com.coolfish.websocket.netty.websocket.handler;

import com.coolfish.websocket.netty.websocket.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author xfh
 * websocket处理器
 */
@Slf4j
@Component
@Scope("prototype")
@ChannelHandler.Sharable
public class NettyWebSocketHandler extends ChannelInboundHandlerAdapter {

    //private IUserService iUserService;
    //@Autowired
    //public NettyWebSocketHandler(IUserService iUserService) {
    //    this.iUserService = iUserService;
    //}

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //  在此处接收客户端发送的信息
        Channel channel = ctx.channel();
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
            String token = textWebSocketFrame.text();
            if (token.isEmpty()) {
                log.info(token + "用户名为空，即将关闭通道");
                ReferenceCountUtil.release(msg);
                channel.close();
                return;
            }
            if (Objects.equals(iUserService.findByUsername(token), null)) {
                log.info(token + "用户名不存在，即将关闭通道");
                ReferenceCountUtil.release(msg);
                channel.close();
                return;
            }
            log.info(token + " exist");
            log.info("来自webSocket客户端" + channel.remoteAddress() + "的信息: " + textWebSocketFrame.text());
        }
        ReferenceCountUtil.release(msg);
    }*/

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("--webSocket--新连接加入--handlerAdded被调用" + ctx.channel().id().asLongText());
        NettyConfig.getChannelGroup().add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("移除webSocket连接，ChannelId：" + ctx.channel().id().asLongText());
        NettyConfig.getChannelGroup().remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("webSocket建立连接");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("webSocket断开连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            log.debug("客户端 " + channel.remoteAddress() + " 断开了连接！");
            log.debug(cause.getMessage());
            // 删除通道
            NettyConfig.getChannelGroup().remove(ctx.channel());
            ctx.close();
        } else {
            ctx.fireExceptionCaught(cause);
            log.debug("-------------------------------------{}",cause);
        }
    }

}
