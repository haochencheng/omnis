package omnis.config.client.event;

import omnis.config.client.context.ConfigClientContext;
import omnis.config.client.context.ConfigClientContextHolder;
import omnis.config.core.context.event.OmnisEvent;
import omnis.config.core.context.event.OmnisEventListener;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-18 09:00
 **/
public class ApplicationContextEventListener implements OmnisEventListener {

    @Override
    public void onOmnisEvent(OmnisEvent event) {
        if (event instanceof ConfigContextStartedEvent){
            ConfigClientContextHolder.setConfigClientContext((ConfigClientContext) event.getSource());
        }
    }
}
