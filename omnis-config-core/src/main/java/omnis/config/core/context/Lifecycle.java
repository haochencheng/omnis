package omnis.config.core.context;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-10 23:10
 **/
public interface Lifecycle {

    void start();

    void stop();

    boolean isRunning();

}
