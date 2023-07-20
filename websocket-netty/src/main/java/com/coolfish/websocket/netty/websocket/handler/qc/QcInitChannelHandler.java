package com.coolfish.websocket.netty.websocket.handler.qc;

import com.coolfish.websocket.netty.websocket.constants.IWebsocketSpecialCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @className: InitChannelHandler
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/1/19
 */
@Component
@ChannelHandler.Sharable
@Scope("prototype")
@Slf4j
public class QcInitChannelHandler extends ChannelInitializer<SocketChannel> {

    private QcWebsocketChannelIdleHandler qcWebsocketChannelIdleHandler;
    private QcNettyWebSocketHandler qcNettyWebSocketHandler;
    private QcExceptionHandler qcExceptionHandler;

    @Autowired
    public QcInitChannelHandler(QcWebsocketChannelIdleHandler qcWebsocketChannelIdleHandler, QcNettyWebSocketHandler qcNettyWebSocketHandler, QcExceptionHandler qcExceptionHandler) {
        this.qcWebsocketChannelIdleHandler = qcWebsocketChannelIdleHandler;
        this.qcNettyWebSocketHandler = qcNettyWebSocketHandler;
        this.qcExceptionHandler = qcExceptionHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        pipeline.addLast("Http-codec", new HttpServerCodec());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        pipeline.addLast("Aggregator", new HttpObjectAggregator(65535));
        // ChunkedWriteHandler：向客户端发送HTML5文件,文件过大会将内存撑爆
        pipeline.addLast("Http-chunked", new ChunkedWriteHandler());
        pipeline.addLast("QcWebSocketAggregator", new WebSocketFrameAggregator(65535));
        //超过10分钟未发生写事件，则关闭连接 写空闲 600秒
        pipeline.addLast(new IdleStateHandler(0, 600, 0, TimeUnit.SECONDS));
        pipeline.addLast("QcWebsocketChannelIdleHandler", qcWebsocketChannelIdleHandler);
        //用于处理websocket, /ws为访问websocket时的uri
        pipeline.addLast("QcWebsocketHandler", qcNettyWebSocketHandler);
        pipeline.addLast("QcProtocolHandler", new WebSocketServerProtocolHandler(IWebsocketSpecialCode.WS_PROTOCOL_QC_SUFFIX, null, true, 65535));
        pipeline.addLast("QcExceptionHandler",qcExceptionHandler);
    }


}
