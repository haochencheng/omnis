package omnis.config.handler;

import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import proto.BaseProto;
import proto.ConfigProto;
import util.Md5Util;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-11 08:34
 **/
@Slf4j
public class ProtoServerHandler extends SimpleChannelInboundHandler<BaseProto.BaseMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseProto.BaseMessage baseMessage) throws Exception {
        try {
            BaseProto.MessageType messageType = baseMessage.getMessageType();
            //TODO 客户端、服务端 默认版本号 逻辑
            switch (messageType){
                case clientOn:
                    // 将服务需要配置发送给客户端
                    mockClientOnResponse(ctx, baseMessage, messageType);
            }
        }finally {
            ReferenceCountUtil.release(baseMessage);
        }
    }


    /**
     * 服务端 mock 客户端clientOn response
     * @param ctx
     * @param baseMessage
     * @param messageType
     * @throws com.google.protobuf.InvalidProtocolBufferException
     */
    private void mockClientOnResponse(ChannelHandlerContext ctx, BaseProto.BaseMessage baseMessage, BaseProto.MessageType messageType) throws com.google.protobuf.InvalidProtocolBufferException {
        ByteString data = baseMessage.getData();
        ConfigProto.ConfigRequest.Builder configRequestBuilder = ConfigProto.ConfigRequest.newBuilder();
        configRequestBuilder.mergeFrom(data);
        ConfigProto.ConfigRequest configRequest = configRequestBuilder.build();
        log.debug("configReques:{},messageType:{}", configRequest.toString(),messageType.toString());
        ConfigProto.ConfigResponse.Builder configResponseBuilder = ConfigProto.ConfigResponse.newBuilder();
        ConfigProto.ConfigInstance.Builder configInstanceBuilder = ConfigProto.ConfigInstance.newBuilder();
        ConfigProto.ConfigInfo configInfo = configRequest.getConfigInfoList(0);
        configInstanceBuilder.setConfigInfo(configInfo);
        String tag = Md5Util.getMD5(configInfo.getGroup(),
                configInfo.getTopic(), configInfo.getConfigName(), configInfo.getVersion());
        log.debug("configInfo tag",tag);
        configInstanceBuilder.setTag(tag);
        configInstanceBuilder.setEnabled(true);
        String properties="aa=bb\r\nuserName=cc";
        configInstanceBuilder.setData(ByteString.copyFrom(properties.getBytes()));
        configResponseBuilder.addConfigInstanceList(configInstanceBuilder.build());
        BaseProto.BaseMessage.Builder baseMessageBuilder = baseMessage.toBuilder();
        baseMessageBuilder.setData(configResponseBuilder.build().toByteString());
        ctx.channel().writeAndFlush(baseMessageBuilder.build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        // 关闭serverChannel
//        ctx.channel().close();
    }
}
