package omnis.config.client.context;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import omnis.config.client.config.ClientConfig;
import omnis.config.client.event.ApplicationContextEventListener;
import omnis.config.client.event.ConfigContextStartedEvent;
import omnis.config.client.peers.ClusterManager;
import omnis.config.core.context.Lifecycle;
import omnis.config.core.context.event.OmnisEvent;
import omnis.config.core.context.event.OmnisEventListener;
import proto.BaseProto;
import proto.ConfigProto;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-14 20:47
 **/
@Slf4j
public class ConfigClientContext implements Lifecycle {

    private ClientConfig clientConfig;

    private ClusterManager clusterManager;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端连接
     */
    private Channel channel;

    private final AtomicBoolean initConfig=new AtomicBoolean();

    private List<OmnisEventListener> clientEventListener=new LinkedList<>();

    /**
     * 是否在运行
     */
    private boolean isRunning=false;

    public ConfigClientContext(ClientConfig clientConfig) {
        if (!clientConfig.isNeedSyncConfig()){
            log.warn("没有需要加载的配置文件");
        }
        this.clientConfig=clientConfig;
    }

    @Override
    public void start() {
        log.debug("初始化客户端上下文");
        //初始化事件监听
        initClientEventListener();
        ClusterManager clusterManager=new ClusterManager(clientConfig);
        clusterManager.start();
        this.clusterManager=clusterManager;
        // 发布contextStart事件
        publish(new ConfigContextStartedEvent(this));
        // 向服务器发起同步信息
        try {
            sendClientSyncConfigMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("客户端发送消息失败:{}",e.getMessage());
        }
    }

    /**
     * 客户端启动连接服务端集群后，向服务端发送同步配置消息
     */
    private void sendClientSyncConfigMessage() throws InterruptedException {
        if (!clientConfig.isNeedSyncConfig()){
            return;
        }
        BaseProto.BaseMessage.Builder baseMessageBuilder = BaseProto.BaseMessage.newBuilder();
        baseMessageBuilder.setMessageType(BaseProto.MessageType.clientOn);
        baseMessageBuilder.setData(clientConfig.getConfigRequestList().toByteString());
        clusterManager.sendMessage(baseMessageBuilder.build());
        synchronized (this){
            log.debug("等待服务端同步配置" );
            wait();
        }
    }

    private void initClientEventListener(){
        this.clientEventListener.add(new ApplicationContextEventListener());
    }

    private void publish(OmnisEvent omnisEvent){
        this.clientEventListener.forEach(omnisEventListener -> {
            omnisEventListener.onOmnisEvent(omnisEvent);
        });
    }



    public void finishInitConfig(){
        initConfig.set(true);
        synchronized (this){
            this.notify();
        }
    }

    private void closeServer(Channel channel) {
        if (Objects.nonNull(channel)) {
            channel.close();
        }
    }

    @Override
    public void stop() {
        closeServer(this.channel);
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isNeedSyncConfig(){
        return this.clientConfig.isNeedSyncConfig();
    }

}
