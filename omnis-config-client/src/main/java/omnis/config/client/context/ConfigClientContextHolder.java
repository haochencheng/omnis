package omnis.config.client.context;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-18 08:53
 **/
public class ConfigClientContextHolder {

    private static ConfigClientContext configClientContext;

    public static ConfigClientContext getConfigClientContext() {
        return configClientContext;
    }

    public static void setConfigClientContext(ConfigClientContext configClientContext) {
        ConfigClientContextHolder.configClientContext = configClientContext;
    }
}
