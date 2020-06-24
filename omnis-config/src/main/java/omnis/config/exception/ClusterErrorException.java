package omnis.config.exception;

/**
 * @description: if omnis.config.store configInstance fail
 * @author: haochencheng
 * @create: 2020-06-09 22:53
 **/
public class ClusterErrorException extends Exception {

    public ClusterErrorException(String message) {
        super(message);
    }
}
