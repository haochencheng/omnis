package omnis.config.core;

import lombok.Data;

import java.nio.channels.Channel;
import java.util.List;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-21 19:58
 **/
@Data
public class ConfigServer {

    /**
     * 服务ip
     */
    private String configServerIp;

    /**
     * 服务端口
     */
    private Integer configServerPort=9081;

    /**
     * 集群服务地址 ip:port
     */
    private List<String> clusterIpList;

    /**
     * 是否集群模式
     */
    private boolean isClusterModel;

    /**
     * 客户端连接
     */
    private List<Channel> clientChannel;

}
