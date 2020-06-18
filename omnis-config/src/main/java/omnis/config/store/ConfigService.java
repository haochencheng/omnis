package omnis.config.store;

import omnis.config.exception.StoreConfigInstanceErrorException;
import proto.ConfigProto;

import java.util.List;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:11
 **/
public interface ConfigService {

    /**
     * get topic form local omnis.config.cache
     * @param configInfo
     * @return
     */
    ConfigProto.ConfigInstance getConfigInstance(ConfigProto.ConfigInfo configInfo);

    /**
     *
     * @param configRequest
     * @return
     */
    ConfigProto.ConfigResponse getConfigInstance(ConfigProto.ConfigRequest configRequest);


    /**
     * set configInstance to omnis-data and local omnis.config.cache
     * then notice the client which subscription the topic
     * if fail throw StoreConfigInstanceErrorException
     * @param configInstance
     * @return
     * @throws StoreConfigInstanceErrorException
     */
    ConfigProto.ConfigInstance addConfigInstance(ConfigProto.ConfigInstance configInstance) throws StoreConfigInstanceErrorException;

    /**
     *
     * @param configInstance
     * @return
     * @throws StoreConfigInstanceErrorException
     */
    ConfigProto.ConfigInstance updateConfigInstance(ConfigProto.ConfigInstance configInstance) throws StoreConfigInstanceErrorException;



}
