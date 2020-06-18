package omnis.config.event;

import omnis.config.core.context.event.OmnisEvent;
import omnis.config.core.context.event.OmnisEventListener;

import java.util.LinkedList;
import java.util.List;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-09 23:32
 **/
public class ConfigInstanceEventSupport implements ConfigInstanceEventPublisher{

    private List<OmnisEventListener> configInstanceEventListenerList;


    public ConfigInstanceEventSupport() {
        this.configInstanceEventListenerList = new LinkedList<>();
    }

    public void add(OmnisEventListener omnisEventListener){
        synchronized (omnisEventListener){
            configInstanceEventListenerList.add(omnisEventListener);
        }
    }

    @Override
    public void publishEvent(OmnisEvent event) {
        for (OmnisEventListener configInstanceEventListener : configInstanceEventListenerList) {
            configInstanceEventListener.onOmnisEvent(event);
        }
    }

}
