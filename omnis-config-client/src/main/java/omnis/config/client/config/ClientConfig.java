package omnis.config.client.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import omnis.config.core.lb.LBStrategy;
import proto.ConfigProto;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-16 22:12
 **/
@Data
@Builder
public class ClientConfig {

    /**
     * 集群服务地址 ip:port
     */
    private List<String> serverUrlList;

    /**
     * 客户端心跳间隔
     */
    private long leaseTime;

    /**
     * 任期 客户端心跳时携带任期，客户端任期低于服务端任期，客户端配置落后，服务端发送更新配置信息。
     * 服务端任期低于客户端任期，服务端从集群同步更新信息
     */
    private long term;

    private ConfigProto.ConfigRequest configRequestList;

    private HashMap<ConfigProto.ConfigInfo,ConfigProto.ConfigInstance> configRequestConfigInstanceHashMap;

    private List<ConfigProto.ConfigInstance> configInstanceList;

    private LBStrategy lbStrategy;

    public boolean isNeedSyncConfig(){
        return Objects.nonNull(configRequestList);
    }

}
