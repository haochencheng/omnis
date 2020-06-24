package omnis.config.resource;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import omnis.config.core.context.resource.FileSystemResource;
import omnis.config.exception.ParamErrorException;
import util.InetUtil;

import java.io.*;
import java.net.URL;
import java.util.*;

import static constants.Constants.*;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-19 22:03
 **/
@Slf4j
public class ResourceManager {


    private List<String> configResourceLocationList;

    public static final String SERVER_PROPERTIES_NOT_FUND = "omnis-config.properties not fund";
    public static final String OMNIS_CONFIG_PROPERTIES = "omnis-config.properties";

    public static final String DEFAULT_OMNIS_CONFIG_LOCATION = "." + PATH_SEPARATOR + "config" + PATH_SEPARATOR + PATH_SEPARATOR;
    public static final String OMNIS_CONFIG_LOCATION = "omnis.config.location";
    public static final String CONFIG_SERVER_IP = "config.server.ip";
    public static final String CONFIG_SERVER_PORT = "config.server.port";
    public static final String CONFIG_CLUSTER_IP = "config.cluster.ip";

    private FileSystemResource fileSystemResource;
    private ConfigResource configResource;

    private String classesPath;

    private HashMap<String, String> propertiesMap = new HashMap();

    /**
     * 配置文件优先级
     * 1 ./config/omnis-config.properties
     * 2. 环境变量 omnis.config.location
     * 3. java -jar 启动参数 -Domnis.config.location=...
     * 4. 项目目录resources中 omnis-config.properties
     */
    public ResourceManager() {
        this.configResource=new ConfigResource();
        this.configResourceLocationList = new ArrayList<>();
        //初始化配置文件路径
        configResourceLocationList.add(DEFAULT_OMNIS_CONFIG_LOCATION);
        String envConfigLocation = System.getenv(OMNIS_CONFIG_LOCATION);
        if (!StringUtil.isNullOrEmpty(envConfigLocation)) {
            configResourceLocationList.add(envConfigLocation);
        }
        String configLocation = System.getProperty(OMNIS_CONFIG_LOCATION);
        if (!StringUtil.isNullOrEmpty(envConfigLocation)) {
            configResourceLocationList.add(configLocation);
        }
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource("");
        String classesPath = url.getPath();
        this.classesPath = classesPath;
        String classesConfigPath = classesPath + OMNIS_CONFIG_PROPERTIES;
        configResourceLocationList.add(classesConfigPath);
    }

    public void loadResource() throws IOException {
        FileSystemResource fileSystemResource = null;
        for (String configPath : configResourceLocationList) {
            fileSystemResource = new FileSystemResource(configPath);
            // 加载配置文件
            if (fileSystemResource.exists()) {
                break;
            }
            fileSystemResource = null;
        }
        if (Objects.isNull(fileSystemResource)) {
            log.error(SERVER_PROPERTIES_NOT_FUND);
            throw new FileNotFoundException(SERVER_PROPERTIES_NOT_FUND);
        }
        this.fileSystemResource = fileSystemResource;
        buildConfigResource();
    }

    /**
     * 构建 config server 配置
     * @throws IOException
     */
    private void buildConfigResource() throws IOException {
        loadProperties();
        buildServerInfo(configResource);
        buildClusterIp(configResource);
    }

    /**
     * 构建config server 相关元数据
     * @param configResource
     * @throws IOException
     */
    private void buildServerInfo(ConfigResource configResource) {
        String configServerIp = propertiesMap.get(CONFIG_SERVER_IP);
        if (StringUtil.isNullOrEmpty(configServerIp)) {
            configServerIp = InetUtil.getSelfIp();
        }
        configResource.setConfigServerIp(configServerIp);
        String configServerPort = propertiesMap.get(CONFIG_SERVER_PORT);
        // 没配置端口 使用默认端口
        if (!StringUtil.isNullOrEmpty(configServerPort)) {
            try {
                configResource.setConfigServerPort(Integer.valueOf(configServerPort));
            }catch (NumberFormatException e){
                throw new RuntimeException("config.server.port must be number");
            }

        }
    }

    /**
     * 构建集群相关配置
     * @param configResource
     * @throws IOException
     */
    private void buildClusterIp(ConfigResource configResource) throws IOException {
        String serverUrlList = propertiesMap.get(CONFIG_CLUSTER_IP);
        ArrayList arrayList = new ArrayList();
        if (StringUtil.isNullOrEmpty(serverUrlList)) {
            configResource.setClusterIpList(arrayList);
            configResource.setModel(ConfigResource.Model.StandAlone);
            return;
        }
        String[] split = serverUrlList.split(COMMA_DIVISION);
        for (String ipPortStr : split) {
            String[] ipPortArray = ipPortStr.split(COLON_DIVISION);
            if (ipPortArray.length == 1) {
                throw new IllegalArgumentException(CONFIG_CLUSTER_IP + " must be ip:port and split with ,");
            }
            arrayList.add(ipPortArray[0] + ":" + ipPortArray[1]);
        }
        configResource.setClusterIpList(arrayList);
        if (split.length == 1) {
            configResource.setModel(ConfigResource.Model.StandAlone);
        } else {
            configResource.setModel(ConfigResource.Model.Cluster);
        }

    }

    private void loadProperties() throws IOException {
        try (InputStream inputStream = fileSystemResource.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String property = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(COMMENT)) {
                    // # 忽略注释
                    continue;
                } else if (line.contains(COMMENT)) {
                    line = line.substring(0, line.indexOf(COMMENT));
                    line = line.trim();
                }
                // 支持多行 \分割
                if (line.endsWith("\\")) {
                    property += line.trim().substring(0,line.length()-1);
                    continue;
                }
                if (StringUtil.isNullOrEmpty(property)) {
                    property = line;
                } else {
                    property += line;
                }
                String[] split = property.split(EQUAL_SIGN_DIVISION);
                propertiesMap.put(split[0], split[1]);
                property = "";
            }
        }
    }

    public ConfigResource getConfigResource() {
        return configResource;
    }
}
