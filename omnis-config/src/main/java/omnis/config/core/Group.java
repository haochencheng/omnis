package omnis.config.core;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-08 23:26
 **/
@Data
public class Group {

    private String group;

    private List<Topic> topicList;

}
