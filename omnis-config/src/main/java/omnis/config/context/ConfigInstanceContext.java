package omnis.config.context;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import omnis.config.core.context.event.OmnisContextStartedEvent;
import omnis.config.core.context.event.OmnisEvent;
import omnis.config.core.context.event.OmnisEventListener;
import omnis.config.event.ConfigInstanceEventPublisher;
import omnis.config.event.ConfigInstanceEventSupport;
import omnis.config.exception.ClusterErrorException;
import omnis.config.handler.ProtoServerInitializer;
import omnis.config.replicate.ClusterManger;
import omnis.config.resource.ConfigResource;
import omnis.config.resource.ResourceManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 22:29
 **/
@Slf4j
public class ConfigInstanceContext implements ConfigInstanceEventPublisher, Lifecycle {

    private boolean isRunning = false;

    private long startupDate;

    private ConfigInstanceEventSupport configInstanceEventSupport;

    private Channel channel;

    private ResourceManager resourceManager;

    private ClusterManger clusterManger;



    public ConfigInstanceContext() {
        this.configInstanceEventSupport = new ConfigInstanceEventSupport();
        this.startupDate=System.currentTimeMillis();

    }

    @Override
    public void publishEvent(OmnisEvent event) {
        this.configInstanceEventSupport.publishEvent(event);
    }

    public void add(OmnisEventListener configInstanceEventListener) {
        configInstanceEventSupport.add(configInstanceEventListener);
    }

    public void add(List<OmnisEventListener> configInstanceEventListenerList) {
        for (OmnisEventListener configInstanceEventListener : configInstanceEventListenerList) {
            configInstanceEventSupport.add(configInstanceEventListener);
        }
    }


    @Override
    public void start()  {
        isRunning = true;
        this.startupDate = System.currentTimeMillis();
        log.info("config server start at "+ LocalDateTime.now());
        //加载配置文件
        ResourceManager resourceManager=new ResourceManager();
        try {
            resourceManager.loadResource();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 获取注册事件
        add(configInstanceEventSupport.getConfigInstanceEventListener());
        // 发送启动事件
        publishEvent(new OmnisContextStartedEvent( this));
        // 加入集群
        clusterManger=new ClusterManger(resourceManager.getConfigResource());
        try {
            clusterManger.clusterMeet();
        } catch (ClusterErrorException e) {
            throw new RuntimeException(e);
        }
        // 服务启动
        serverBootstrap();
    }

    private void serverBootstrap() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ProtoServerInitializer());
            ChannelFuture channelFuture ;
            try {
                // 获取本地 ip 端口
                ConfigResource configResource = resourceManager.getConfigResource();
                String configServerIp = configResource.getConfigServerIp();
                Integer configServerPort = configResource.getConfigServerPort();
                channelFuture = serverBootstrap.bind(configServerIp, configServerPort).sync();
                this.channel = channelFuture.channel();
                log.info("config server start success,the ip is {},the port is {}",configServerIp,configServerPort);
                this.channel.closeFuture().sync();
            }catch (InterruptedException e){
                log.error("config server start Interrupted ",e);
                throw new RuntimeException(e);
            }
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    @Override
    public void stop() {
        isRunning = false;
        clusterManger.stop();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


}
