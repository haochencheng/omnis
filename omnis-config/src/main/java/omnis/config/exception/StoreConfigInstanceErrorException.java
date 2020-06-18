package omnis.config.exception;

/**
 * @description: if omnis.config.store configInstance fail
 * @author: haochencheng
 * @create: 2020-06-09 22:53
 **/
public class StoreConfigInstanceErrorException extends Exception {

    public StoreConfigInstanceErrorException(String message) {
        super(message);
    }
}
