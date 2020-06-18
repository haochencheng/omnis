package omnis.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import omnis.config.context.ConfigInstanceContext;
import omnis.config.context.Lifecycle;
import omnis.config.handler.ProtoServerInitializer;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-10 23:57
 **/
public class ConfigServer implements Lifecycle {

    ConfigInstanceContext configInstanceContext;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ProtoServerInitializer());
            ChannelFuture channelFuture ;
            try {
                channelFuture = serverBootstrap.bind(9810).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void start() {
        this.configInstanceContext = createdConfigInstanceContext();
        configInstanceContext.start();
    }

    @Override
    public void stop() {
        configInstanceContext.stop();
    }

    @Override
    public boolean isRunning() {
        return false;
    }


    private ConfigInstanceContext createdConfigInstanceContext(){
        return new ConfigInstanceContext();
    }

}
