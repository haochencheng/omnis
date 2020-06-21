package omnis.config.context;

import java.io.IOException;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-10 23:10
 **/
public interface Lifecycle {

    String START_EVENT = "start";

    String STOP_EVENT = "stop";

    String ADD_CONFIG_INSTANCE_EVENT = "add";

    String UPDATE_CONFIG_INSTANCE_EVENT = "update";

    String ON_LINE_CONFIG_INSTANCE_EVENT = "online";

    String OFF_LINE_CONFIG_INSTANCE_EVENT = "offline";

    void start() throws IOException;

    void stop();

    boolean isRunning();

}
