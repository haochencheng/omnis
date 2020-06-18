package omnis.config.client.handler;

import proto.BaseProto;

public interface ClientHandler {

    void handle(BaseProto.BaseMessage baseMessage);

}
