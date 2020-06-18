package omnis.config.client.handler;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import omnis.config.client.context.ConfigClientContext;
import omnis.config.client.context.ConfigClientContextHolder;
import proto.BaseProto;
import proto.ConfigProto;

import java.util.List;

/**
 * 客户端启动接收服务端 同步配置回调
 * @description:
 * @author: haochencheng
 * @create: 2020-06-17 23:25
 **/
@Slf4j
public class ClientOnHandler implements ClientHandler{

    @Override
    public void handle(BaseProto.BaseMessage baseMessage)   {
        ByteString data = baseMessage.getData();
        try {
            ConfigProto.ConfigResponse configResponse = ConfigProto.ConfigResponse.parseFrom(data);
            List<ConfigProto.ConfigInstance> configInstanceListList = configResponse.getConfigInstanceListList();
            if (configInstanceListList.isEmpty()){
                log.error("no config response from server");
                return;
            }
            ConfigProto.ConfigInstance configInstance = configInstanceListList.get(0).toBuilder().build();
            log.info("接收到服务器配置tag：{},data:{}",configInstance.getTag(),configInstance.getData().toString());
            //接收到配置文件 通知/唤醒主线程继续执行
            ConfigClientContextHolder.getConfigClientContext().finishInitConfig();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            log.error("服务端发送错误的消息类型");
            //TODO 尝试从本地加载 加载失败抛出 Runtime异常

        }
    }

}
