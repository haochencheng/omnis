package omnis.config.store;


import local.LocalStageServer;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-09 08:53
 **/
public class DataStoreFactory {

    private static DataServer dataServer=new LocalStageServer();

    public static DataServer getDataStore(){
        return dataServer;
    }



}
