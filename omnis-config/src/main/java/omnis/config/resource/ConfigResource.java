package omnis.config.resource;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-19 21:35
 **/
@Data
public class ConfigResource {

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
    private List<String> serverUrlList;

    /**
     * 单机还是集群
     */
    private Model model;

    public boolean isClusterModel(){
        return model.equals(Model.Cluster);
    }

    enum Model {
        StandAlone,
        Cluster;
    }

}
