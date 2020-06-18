package omnis.config.store;

import omnis.config.core.DataResponse;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:01
 **/
public interface DataServer<T> {

    T getData(String tag);

    DataResponse<T> setData(String tag, T data);

}
