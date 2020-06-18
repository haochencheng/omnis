package omnis.config.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import omnis.config.client.exception.InvalidMessageException;
import proto.BaseProto;

import java.util.HashMap;
import java.util.Objects;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-11 08:45
 **/
@Slf4j
public class ProtoClientHandler extends SimpleChannelInboundHandler<BaseProto.BaseMessage> {

    private HashMap<BaseProto.MessageType,ClientHandler> clientHandlerHashMap;

    public ProtoClientHandler() {
        clientHandlerHashMap=new HashMap<>();
        clientHandlerHashMap.put(BaseProto.MessageType.clientOn,new ClientOnHandler());

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseProto.BaseMessage baseMessage) throws InvalidMessageException {
        try {
            BaseProto.MessageType messageType = baseMessage.getMessageType();
            ClientHandler clientHandler = clientHandlerHashMap.get(messageType);
            if (Objects.isNull(clientHandler)){
                log.error("服务端消息错误，没有对应的处理器.{}",baseMessage.getMessageType());
                throw new InvalidMessageException("服务端消息错误，没有对应的处理器");
            }
            clientHandler.handle(baseMessage);
            log.info("收到服务端消息,{},{}",messageType.toString(),baseMessage.getBodyType().toString());
        } finally {
            ReferenceCountUtil.release(baseMessage);
        }

    }

    /**
     * 客户端启动 发送 拉取服务端配置消息
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("channelActive");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //记录错误日志 TODO
        // 关闭serverChannel
        ctx.channel().close();
    }
}
