package omnis.config.client.peers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import omnis.config.client.config.ClientConfig;
import omnis.config.client.handler.ProtoClientInitializer;
import omnis.config.client.lb.ClientLoadBalance;
import omnis.config.core.context.Lifecycle;
import proto.BaseProto;
import proto.ConfigProto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-16 22:31
 **/
@Slf4j
public class ClusterManager implements Lifecycle {

    private ClientConfig clientConfig;

    /**
     * 服务端集群服务器列表
     */
    private List<ServerInstance> serverInstanceList;

    private ConcurrentHashMap<String,ServerInstance> serverInstanceHashMap=new ConcurrentHashMap<>();

    /**
     * 心跳线程池
     */
    private ScheduledExecutorService heartBeatScheduledService;

    private ExecutorService clientExecutorService;

    private AtomicBoolean isRunning=new AtomicBoolean();

    private ClientLoadBalance clientLoadBalance;

    private  CountDownLatch connectClusterServerCountDownLatch;

    public ClusterManager(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        int size = clientConfig.getServerUrlList().size();
        this.heartBeatScheduledService = new ScheduledThreadPoolExecutor(1);
        this.clientExecutorService=Executors.newFixedThreadPool(size, r -> new Thread(new ThreadGroup("client-executor"), r));
        this.serverInstanceList=new ArrayList<>(size);
        this.clientLoadBalance=new ClientLoadBalance(clientConfig.getLbStrategy());
    }

    @Override
    public void start() {
        List<String> serverUrlList = clientConfig.getServerUrlList();
        int size = serverUrlList.size();
        connectClusterServerCountDownLatch=new CountDownLatch(size);
        log.debug("连接服务器集群数量：{}",size );
        serverUrlList.forEach(serverUrl-> clientExecutorService.submit(()->connectServer(serverUrl)));
        try {
            connectClusterServerCountDownLatch.await(2100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("连接集群超时",e.getMessage());
        }
    }

    private void connectServer(String serverUrl) {
        String[] ipPortSplit = serverUrl.split(":");
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Channel channel;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ProtoClientInitializer());
            int serverPort = Integer.parseInt(ipPortSplit[1]);
            String serverIp = ipPortSplit[0];
            ChannelFuture channelFuture = bootstrap.connect(serverIp, serverPort).sync();
            if (!channelFuture.awaitUninterruptibly(2, TimeUnit.SECONDS)){
                throw new InterruptedException();
            }
            channel = channelFuture.channel();
            String serverId = channel.id().asLongText();
            log.info("客户端连接成功,服务端id：{}",channel.id());
            ServerInstance serverInstance=new ServerInstance();
            serverInstance.setServerPort(serverPort);
            serverInstance.setServerIp(serverIp);
            serverInstance.setServerId(serverId);
            serverInstance.setChannel(channel);
            serverInstanceList.add(serverInstance);
            serverInstanceHashMap.put(serverId,serverInstance);
            //开启心跳
            startHeartBeat(channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
            connectClusterServerCountDownLatch.countDown();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return this.isRunning.get();
    }

    private void startHeartBeat(Channel channel){
        BaseProto.BaseMessage baseMessageBuild = getBaseMessage();
        heartBeatScheduledService.scheduleAtFixedRate(()->{
            try {
                if (isRunning()){
                    ChannelFuture channelFuture = channel.writeAndFlush(baseMessageBuild);
                    channelFuture.addListener((ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            // do sth
                            log.debug("发送成功");
                        } else {
                            // do sth
                            log.error("发送失败");
                        }
                    });

                }
            }catch (Throwable e){
                e.printStackTrace();
            }
        }, 0, clientConfig.getLeaseTime(), TimeUnit.MILLISECONDS);
    }

    private BaseProto.BaseMessage getBaseMessage() {
        ConfigProto.ConfigRequest.Builder configRequestBuilder = ConfigProto.ConfigRequest.newBuilder();
        BaseProto.BaseMessage.Builder baseMessage = BaseProto.BaseMessage.newBuilder();
        baseMessage.setMessageType(BaseProto.MessageType.heartBeat);
        baseMessage.setBodyType(BaseProto.BodyType.add);
        baseMessage.setMessageId("");
        ConfigProto.ConfigInfo.Builder configInfoBuilder = ConfigProto.ConfigInfo.newBuilder();
        configInfoBuilder.setConfigName("aa");
        configInfoBuilder.setGroup("bb");
        configInfoBuilder.setTopic("cc");
        configInfoBuilder.setVersion("v1.0");
        configRequestBuilder.addConfigInfoList(configInfoBuilder);
        baseMessage.setData(configRequestBuilder.build().toByteString());
        return baseMessage.build();
    }

    public ChannelFuture sendMessage(Object data){
        ServerInstance serverInstance = clientLoadBalance.chooseServer(serverInstanceList);
        return serverInstance.getChannel().writeAndFlush(data);
    }
}
