package onmis.config.client;

import lombok.extern.slf4j.Slf4j;
import omnis.config.client.config.ClientConfig;
import omnis.config.client.context.ConfigClientContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import proto.ConfigProto;

import java.util.Arrays;



/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-09 22:13
 **/
@Slf4j
public class ConfigClientTest {

    @DisplayName("客户端连接测试")
    @Test
    public void protoClient() throws InterruptedException {
        ClientConfig.ClientConfigBuilder clientConfigBuilder = ClientConfig.builder();
        clientConfigBuilder.leaseTime(5000);
        clientConfigBuilder.serverUrlList(Arrays.asList("localhost:9810"));
        // 没有ConfigRequest 不会同步配置
        ConfigClientContext configClientContext=new ConfigClientContext(clientConfigBuilder.build());
        configClientContext.start();
        if (configClientContext.isNeedSyncConfig()){
            synchronized (this){
                wait();
            }
        }
        log.info("客户端启动成功");
    }

    @DisplayName("客户端拉取服务端配置文件")
    @Test
    public void clientSyncConfig() throws InterruptedException {
        ClientConfig.ClientConfigBuilder clientConfigBuilder = ClientConfig.builder();
        clientConfigBuilder.leaseTime(5000);
        clientConfigBuilder.serverUrlList(Arrays.asList("localhost:9810"));
        // 构建ConfigRequest 同步配置
        ConfigProto.ConfigRequest.Builder configRequestBuilder = ConfigProto.ConfigRequest.newBuilder();
        ConfigProto.ConfigInfo.Builder configInfoBuilder = ConfigProto.ConfigInfo.newBuilder();
        configInfoBuilder.setGroup("test");
        configInfoBuilder.setTopic("omnis-user");
        configInfoBuilder.setConfigName("application.properties");
        configInfoBuilder.setVersion("V1.0");
        configRequestBuilder.addConfigInfoList(configInfoBuilder);
        clientConfigBuilder.configRequestList(configRequestBuilder.build());
        ConfigClientContext configClientContext=new ConfigClientContext(clientConfigBuilder.build());
        configClientContext.start();
        log.info("客户端启动成功");
    }


}
