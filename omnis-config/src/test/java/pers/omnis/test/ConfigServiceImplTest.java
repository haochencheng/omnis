package pers.omnis.test;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import omnis.config.ConfigBootStrap;
import omnis.config.context.ConfigInstanceContext;
import omnis.config.exception.StoreConfigInstanceErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import proto.ConfigProto;
import omnis.config.store.ConfigServiceImpl;
import util.Md5Util;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-09 22:13
 **/
@Slf4j
public class ConfigServiceImplTest {

    @DisplayName("获取tag")
    @Test
    public void getTag() {
        ConfigProto.ConfigInfo.Builder builder = ConfigProto.ConfigInfo.newBuilder();
        builder.setGroup("test");
        builder.setTopic("user");
        builder.setConfigName("mysql.properties");
        builder.setVersion("V1.0");
        String tag = Md5Util.getMD5(builder.getGroup(),
                builder.getTopic(), builder.getConfigName(), builder.getVersion());
        System.out.println(tag);
        Assertions.assertEquals("8D9C556831CF2813EE55C5E6A8E6FCF9", tag);
    }

    @DisplayName("启动上下文")
    @Test
    public void startConfigInstanceContext() throws IOException {
        ConfigBootStrap.bootStrap(new String[0]);
    }

    @DisplayName("获取配置中心配置文件")
    @Test
    public void getTopic() {
        ConfigInstanceContext configInstanceContext = new ConfigInstanceContext();
        configInstanceContext.start();
        ConfigServiceImpl configService = new ConfigServiceImpl();
        ConfigProto.ConfigInfo.Builder configInfoBuilder = ConfigProto.ConfigInfo.newBuilder();
        configInfoBuilder.setGroup("test");
        configInfoBuilder.setTopic("user");
        configInfoBuilder.setConfigName("mysql.properties");
        configInfoBuilder.setVersion("V1.0");
        ConfigProto.ConfigInstance.Builder configInstanceBuilder = ConfigProto.ConfigInstance.newBuilder();
        configInstanceBuilder.setConfigInfo(configInfoBuilder.build());
        configInstanceBuilder.setData(ByteString.copyFrom("aa=bb", Charset.defaultCharset()));
        configInstanceBuilder.setEnabled(true);
        try {
            configService.addConfigInstance(configInstanceBuilder.build());
        } catch (StoreConfigInstanceErrorException e) {
            e.printStackTrace();
        }
        ConfigProto.ConfigInstance configInstance = configService.getConfigInstance(configInfoBuilder.build());
        System.out.println(configInstance.toString());
    }

    @Test
    public void clusterCount(){
        System.out.println(3>>1);
        System.out.println(4>>1);
    }

}
