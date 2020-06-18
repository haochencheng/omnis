package local;

import omnis.config.core.DataResponse;
import omnis.config.store.DataServer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:01
 **/
public class LocalStageServer  implements DataServer {

    private ConcurrentHashMap<String,Object> concurrentHashMap=new ConcurrentHashMap<>();

    @Override
    public Object getData(String tag) {
        return concurrentHashMap.get(tag);
    }

    @Override
    public DataResponse setData(String tag, Object data) {
        concurrentHashMap.put(tag,data);
        return DataResponse.successful();
    }
}
