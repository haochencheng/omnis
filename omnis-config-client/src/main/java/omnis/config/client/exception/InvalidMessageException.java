package omnis.config.client.exception;

/**
 * 错误的消息，接收到服务端的消息无法处理
 * @description:
 * @author: haochencheng
 * @create: 2020-06-18 08:12
 **/
public class InvalidMessageException extends Exception {

    public InvalidMessageException(String message) {
        super(message);
    }
}
