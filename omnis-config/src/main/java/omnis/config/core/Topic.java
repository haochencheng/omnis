package omnis.config.core;


import proto.ConfigProto;

import java.util.List;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:28
 **/
public class Topic {

    /**
     * topic
     */
    private String topic;

    /**
     * omnis.config.server config list
     */
    private List<ConfigProto.ConfigInstance> configInstance;

    /**
     * ip
     */
    private String ip;

    /**
     * vip
     */
    private String vip;

    /**
     * port
     */
    private Integer port;



}
