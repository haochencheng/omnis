package omnis.config.context;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import omnis.config.core.context.event.OmnisContextStartedEvent;
import omnis.config.core.context.event.OmnisEvent;
import omnis.config.core.context.event.OmnisEventListener;
import omnis.config.event.ConfigInstanceEventPublisher;
import omnis.config.event.ConfigInstanceEventSupport;
import omnis.config.exception.ParamErrorException;
import omnis.config.handler.ProtoServerInitializer;
import omnis.config.resource.ResourceManager;
import omnis.config.store.ConfigServiceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 22:29
 **/
@Slf4j
public class ConfigInstanceContext implements ConfigInstanceEventPublisher, Lifecycle {

    private boolean isRunning = false;

    private long startupDate;

    private ConfigInstanceEventSupport configInstanceEventSupport;

    private Channel channel;

    private ResourceManager resourceManager;

    public ConfigInstanceContext() {
        this.configInstanceEventSupport = new ConfigInstanceEventSupport();
        this.startupDate=System.currentTimeMillis();

    }

    @Override
    public void publishEvent(OmnisEvent event) {
        this.configInstanceEventSupport.publishEvent(event);
    }

    public void add(OmnisEventListener configInstanceEventListener) {
        configInstanceEventSupport.add(configInstanceEventListener);
    }

    public void add(List<OmnisEventListener> configInstanceEventListenerList) {
        for (OmnisEventListener configInstanceEventListener : configInstanceEventListenerList) {
            configInstanceEventSupport.add(configInstanceEventListener);
        }
    }


    @Override
    public void start()  {
        isRunning = true;
        this.startupDate = System.currentTimeMillis();
        log.info("config server start at "+ LocalDateTime.now());
        //加载配置文件
        ResourceManager resourceManager=new ResourceManager();
        try {
            resourceManager.loadResource();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 获取注册事件
        add(getConfigInstanceEventListener());
        // 发送启动事件
        publishEvent(new OmnisContextStartedEvent( this));
        // 加入集群

        // 服务启动
        serverBootstrap();
    }

    private void serverBootstrap() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ProtoServerInitializer());
            ChannelFuture channelFuture ;
            try {
                // 获取本地 ip 端口
                channelFuture = serverBootstrap.bind(9810).sync();
                this.channel = channelFuture.channel();
                this.channel.closeFuture().sync();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void sync(){
        try {
            this.channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<OmnisEventListener> getConfigInstanceEventListener() {
        ArrayList<Class<?>> interfaceImpls = getInterfaceImpls(OmnisEventListener.class);
        return Arrays.asList(new ConfigServiceImpl());
    }

    /**
     * 根据接口类获取所有实现类
     *
     * @param target
     * @return
     */
    public static ArrayList<Class<?>> getInterfaceImpls(Class<?> target) {
        ArrayList<Class<?>> subClasses = new ArrayList<>();
        try {
            // 判断class对象是否是一个接口
            if (!target.isInterface()) {
                throw new ParamErrorException("Class对象不是一个interface");
            }
            String basePackage = target.getClassLoader().getResource("").getPath();
            File[] files = new File(basePackage).listFiles();
            // 存放class路径的list
            ArrayList<String> classpaths = new ArrayList<>();
            for (File file : files) {
                // 扫描项目编译后的所有类
                if (file.isDirectory()) {
                    listPackages(file.getName(), classpaths);
                }
            }
            // 获取所有类,然后判断是否是 target 接口的实现类
            for (String classpath : classpaths) {
                Class<?> classObject = Class.forName(classpath);
                // 判断该对象的父类是否为null
                if (classObject.getSuperclass() == null) {
                    continue;
                }
                Set<Class<?>> interfaces = new HashSet<>(Arrays.asList(classObject.getInterfaces()));
                if (interfaces.contains(target)) {
                    subClasses.add(Class.forName(classObject.getName()));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return subClasses;
    }

    /**
     * 获取项目编译后的所有的.class的字节码文件
     * 这么做的目的是为了让 Class.forName() 可以加载类
     *
     * @param basePackage 默认包名
     * @param classes     存放字节码文件路径的集合
     * @return
     */
    public static void listPackages(String basePackage, List<String> classes) {
        URL url = ConfigInstanceContext.class.getClassLoader()
                .getResource("./" + basePackage.replaceAll("\\.", "/"));
        File directory = new File(url.getFile());
        for (File file : directory.listFiles()) {
            // 如果是一个目录就继续往下读取(递归调用)
            if (file.isDirectory()) {
                listPackages(basePackage + "." + file.getName(), classes);
            } else {
                // 如果不是一个目录,判断是不是以.class结尾的文件,如果不是则不作处理
                String classpath = file.getName();
                if (".class".equals(classpath.substring(classpath.length() - ".class".length()))) {
                    classes.add(basePackage + "." + classpath.replaceAll(".class", ""));
                }
            }
        }
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


}
