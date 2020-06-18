package omnis.config.core;

import constants.Constants;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:08
 **/
@Data
@Builder
public class DataResponse<T> implements Serializable {

    private Integer status;
    private String desc;
    private T data;

    public static DataResponse successful(){
        return new DataResponse(Constants.SUCCESS, "", Constants.EMPTY_OBJECT);
    }

    public boolean isSuccess(){
        return Constants.SUCCESS.equals(this.status);
    }

    public boolean isError(){
        return !Constants.SUCCESS.equals(this.status);
    }

}
