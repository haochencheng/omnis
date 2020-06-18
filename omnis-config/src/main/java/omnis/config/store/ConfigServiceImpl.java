package omnis.config.store;

import omnis.config.cache.LocalCacheService;
import omnis.config.context.ConfigInstanceContext;
import omnis.config.core.DataResponse;
import omnis.config.core.context.event.OmnisContextStartedEvent;
import omnis.config.core.context.event.OmnisEvent;
import omnis.config.core.context.event.OmnisEventListener;
import omnis.config.event.AddOmnisConfigEvent;
import omnis.config.exception.StoreConfigInstanceErrorException;
import proto.ConfigProto;
import util.Md5Util;


/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:11
 **/
public class ConfigServiceImpl implements ConfigService, OmnisEventListener {


    private DataServer<ConfigProto.ConfigInstance> dataStore ;

    private ConfigInstanceContext configInstanceContext;


    public ConfigServiceImpl() {
        this.dataStore =  DataStoreFactory.getDataStore();
    }

    @Override
    public ConfigProto.ConfigInstance getConfigInstance(ConfigProto.ConfigInfo configRequest){
        String configName = configRequest.getConfigName();
        String group = configRequest.getGroup();
        String topic = configRequest.getTopic();
        String version = configRequest.getVersion();
        String tag = Md5Util.getMD5(group, topic, configName, version);
        return LocalCacheService.getTopic(tag);
    }

    @Override
    public ConfigProto.ConfigResponse getConfigInstance(ConfigProto.ConfigRequest configRequest) {
        return null;
    }

    @Override
    public ConfigProto.ConfigInstance addConfigInstance(ConfigProto.ConfigInstance configInstance) throws StoreConfigInstanceErrorException {
        ConfigProto.ConfigInfo configInfo = configInstance.getConfigInfo();
        String tag = Md5Util.getMD5(configInfo.getGroup(),
                configInfo.getTopic(), configInfo.getConfigName(), configInfo.getVersion());
        configInstance=configInstance.toBuilder().setTag(tag).build();
        DataResponse<ConfigProto.ConfigInstance> dataResponse = dataStore.setData(tag, configInstance);
        if (dataResponse.isError()){
            throw new StoreConfigInstanceErrorException("omnis.config.store configInstance error");
        }
        LocalCacheService.setTopic(tag,configInstance);
        // send add omnis.config.event
        configInstanceContext.publishEvent(new AddOmnisConfigEvent(configInstance) {
        });
        // notice client

        return configInstance;

    }

    @Override
    public ConfigProto.ConfigInstance updateConfigInstance(ConfigProto.ConfigInstance configInstance) throws StoreConfigInstanceErrorException {
        // send update omnis.config.event

        // notice client
        return null;
    }

    @Override
    public void onOmnisEvent(OmnisEvent event) {
        if (event instanceof OmnisContextStartedEvent){
            this.configInstanceContext= (ConfigInstanceContext) event.getSource();
        }
    }
}
