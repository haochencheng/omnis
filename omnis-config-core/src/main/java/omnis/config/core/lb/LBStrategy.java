package omnis.config.core.lb;


/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-17 23:06
 **/
public enum  LBStrategy {

    /**
     * 轮询
     */
    RoundRobin,
    /**
     * 随机
     */
    Random,
    /**
     * hash
     */
    Hash,
    /**
     * 最短响应时间LRT
     */
    LRT;


}
