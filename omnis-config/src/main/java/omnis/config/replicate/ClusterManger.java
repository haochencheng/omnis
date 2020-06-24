package omnis.config.replicate;

import constants.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import omnis.config.core.ClusterServer;
import omnis.config.core.ConfigServer;
import omnis.config.exception.ClusterErrorException;
import omnis.config.exception.ParamErrorException;
import omnis.config.handler.ProtoServerInitializer;
import omnis.config.resource.ConfigResource;
import org.omg.CORBA.TIMEOUT;
import proto.BaseProto;
import util.InetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-21 20:05
 **/
@Slf4j
public class ClusterManger {

    public static final int CLUSTER_MEET_TIMEOUT = 2000;
    private ConfigResource configResource;

    private ScheduledExecutorService clusterHeartBeetScheduledService;

    private CountDownLatch connectClusterServerCountDownLatch;

    /**
     * 集群服务列表
     */
    private List<ClusterServer> clusterServerList;
    private ConcurrentHashMap<String, ClusterServer> clusterServerHashMap;

    public ClusterManger(ConfigResource configResource) {
        this.configResource = configResource;
        clusterServerHashMap = new ConcurrentHashMap<>();
        List<String> clusterIpList = configResource.getClusterIpList();
        connectClusterServerCountDownLatch = new CountDownLatch(clusterIpList.size() - 1);
        clusterServerList=new ArrayList<>(clusterIpList.size());
        clusterHeartBeetScheduledService = Executors.newScheduledThreadPool(clusterIpList.size() * 2);
    }


    /**
     * 加入集群
     */
    public void clusterMeet() throws ClusterErrorException {
        try {
            startJoinCluster();
        }catch (Exception e){
            stop();
            throw e;
        }

    }

    private void startJoinCluster() throws ClusterErrorException {
        List<String> clusterIpList = configResource.getClusterIpList();
        clusterIpList.forEach(ipPortStr -> {
            String[] split = ipPortStr.split(Constants.COLON_DIVISION);
            String ip = split[0];
            String portStr = split[1];
            if (!InetUtil.isIP(ip)) {
                stop();
                throw new RuntimeException(new ParamErrorException("cluster ip " + ip + " is not ip "));
            }
            int port;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException(new ParamErrorException("cluster port:" + portStr + " must be number"));
            }
            if (ip.equals(configResource.getConfigServerIp())) {
                return;
            }
            // join cluster
            ClusterServer clusterServer = new ClusterServer(ip, port, clusterIpList);
            clusterHeartBeetScheduledService.submit(() -> connectClusterServer(clusterServer));
        });
        try {
            connectClusterServerCountDownLatch.await(CLUSTER_MEET_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("连接集群超时", e);
        }
        //连接成功超过 1/2 就算成功
        if (clusterServerList.isEmpty()){
            // 集群不可用
            return;
        }
        if (clusterServerList.size()>>1<clusterIpList.size()>>1){
            // 集群不可用
            return;
        }
        log.debug("cluster meet success");
    }


    public void connectClusterServer(ClusterServer clusterServer)  {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Channel channel;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ProtoServerInitializer());
            String configServerIp = clusterServer.getConfigServerIp();
            Integer configServerPort = clusterServer.getConfigServerPort();
            ChannelFuture channelFuture = bootstrap.connect(configServerIp, configServerPort).sync();
            if (!channelFuture.awaitUninterruptibly(CLUSTER_MEET_TIMEOUT, TimeUnit.SECONDS)) {
                log.error("连接集群服务器{}:{}超时", configServerIp, configServerPort);
                throw new InterruptedException();
            }
            channel = channelFuture.channel();
            String serverId = channel.id().asLongText();
            log.debug("连接集群服务器{}:{}成功,服务端id：{}", configServerIp, configServerPort, channel.id());
            clusterServer.setServerId(serverId);
            clusterServer.setClusterChannel(channel);
            clusterServerList.add(clusterServer);
            clusterServerHashMap.put(serverId, clusterServer);
            //开启心跳
            startHeartBeat(channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("connect cluster Interrupted :{}",e);
            //连接重试？
        } finally {
            eventLoopGroup.shutdownGracefully();
            connectClusterServerCountDownLatch.countDown();
        }
    }

    private void startHeartBeat(Channel channel) {
        BaseProto.BaseMessage baseMessageBuild = getBaseMessage();
        clusterHeartBeetScheduledService.scheduleAtFixedRate(() -> {
            try {
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
            } catch (Throwable e) {
                log.error("cluster heartBeat error:{}",e);
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    private BaseProto.BaseMessage getBaseMessage() {
        return null;
    }

    public void stop(){
        clusterHeartBeetScheduledService.shutdownNow();
    }

}
