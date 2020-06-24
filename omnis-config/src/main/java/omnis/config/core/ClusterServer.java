package omnis.config.core;

import io.netty.channel.Channel;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-21 19:58
 **/
@Data
public class ClusterServer {

    /**
     * 服务ip
     */
    private String configServerIp;

    /**
     * 服务端口
     */
    private Integer configServerPort;

    /**
     * 集群服务地址 ip:port
     */
    private List<String> clusterIpList;

    private String serverId;

    /**
     * 服务器状态
     */
    private int status;


    /**
     * 客户端连接
     */
    private Channel clusterChannel;

    public ClusterServer(String configServerIp, Integer configServerPort, List<String> clusterIpList) {
        this.configServerIp = configServerIp;
        this.configServerPort = configServerPort;
        this.clusterIpList = clusterIpList;
    }
}
