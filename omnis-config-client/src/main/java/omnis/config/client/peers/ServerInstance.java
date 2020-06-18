package omnis.config.client.peers;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-16 22:28
 **/
@Data
public class ServerInstance {

    /**
     * 服务端id
     */
    private String serverId;

    /**
     * 服务端任期
     */
    private String term;

    /**
     * 服务端地址
     */
    private String serverIp;

    /**
     * 服务端端口
     */
    private Integer serverPort;

    /**
     * 上一次心跳时间
     */
    private long lastHeartBeatTime;

    /**
     * 服务端连接
     */
    private Channel channel;


}
