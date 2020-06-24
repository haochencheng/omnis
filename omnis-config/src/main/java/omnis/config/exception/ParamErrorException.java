package omnis.config.exception;

/**
 * @description: if omnis.config.store configInstance fail
 * @author: haochencheng
 * @create: 2020-06-09 22:53
 **/
public class ParamErrorException extends Exception {

    public ParamErrorException(String message) {
        super(message);
    }

    public ParamErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
