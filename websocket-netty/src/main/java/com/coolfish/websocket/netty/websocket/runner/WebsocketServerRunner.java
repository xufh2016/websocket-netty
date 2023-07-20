package com.coolfish.websocket.netty.websocket.runner;

import com.coolfish.websocket.netty.websocket.handler.InitChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @className: WebsocketServerRunner
 * @description: Websocket服务
 * @author: xufh
 * @date: 2022/5/5
 */
@Component
@Slf4j
public class WebsocketServerRunner implements ApplicationRunner {
    @Value("${netty.socket.port}")
    private int port;
    private InitChannelHandler initChannelHandler;
    private TaskExecutor taskExecutor;
    //NioEventLoopGroup bossGroup;
    //NioEventLoopGroup workerGroup;

    @Autowired
    public WebsocketServerRunner(InitChannelHandler initChannelHandler, @Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.initChannelHandler = initChannelHandler;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskExecutor.execute(this::bootStrap);
    }

    /**
     * websocket服务具体实现方法
     * websocket服务端口号改为8083
     */
    public void bootStrap() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(initChannelHandler)
                    .bind(port).sync();
            if (channelFuture.isSuccess()) {
                log.info("Websocket端口已启动，端口号是：{}", port);
            } else {
                log.info("Websocket启动失败");
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("-------------InterruptedException-------------{}", e.getMessage());
        } finally {
            //优雅关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

   /* @PreDestroy
    public void shutdownLoopGroup() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
    }*/

}
