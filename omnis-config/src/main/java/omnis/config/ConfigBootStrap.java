package omnis.config;

import lombok.extern.slf4j.Slf4j;
import omnis.config.context.ConfigInstanceContext;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-10 23:57
 **/
@Slf4j
public class ConfigBootStrap {

    public static void main(String[] args) {
        bootStrap(args);
    }

    public static void bootStrap(String[] args)  {
        log.info("server bootStrap with args:{}",args);
        ConfigInstanceContext configInstanceContext = new ConfigInstanceContext();
        configInstanceContext.start();
    }


}
