package omnis.config.cache;

import proto.ConfigProto;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:44
 **/
public class LocalCacheService {

    private final static ConcurrentHashMap<String, ConfigProto.ConfigInstance> configInstanceHashMap=new ConcurrentHashMap<>();

    public static ConfigProto.ConfigInstance getTopic(String tag){
        return configInstanceHashMap.get(tag);
    }

    public static void setTopic(String tag, ConfigProto.ConfigInstance configInstance){
         configInstanceHashMap.put(tag,configInstance);
    }


}
