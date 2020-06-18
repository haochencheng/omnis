package omnis.config.client.lb;

import omnis.config.client.peers.ServerInstance;
import omnis.config.core.lb.LBStrategy;
import omnis.config.core.lb.LoadBalabce;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-17 23:04
 **/
public class ClientLoadBalance implements LoadBalabce<ServerInstance> {

    private LBStrategy lbStrategy;

    public ClientLoadBalance(LBStrategy lbStrategy) {
        if (Objects.isNull(lbStrategy)){
            lbStrategy=LBStrategy.Random;
        }
        this.lbStrategy = lbStrategy;
    }

    /**
     * 根据不同策略选择不同服务型 发送消息
     * @param list
     * @return
     */
    @Override
    public ServerInstance chooseServer(List<ServerInstance> list) {
        switch (lbStrategy){
            //TODO

        }
        return list.get(0);
    }
}
