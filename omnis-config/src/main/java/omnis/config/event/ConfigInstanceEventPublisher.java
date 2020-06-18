package omnis.config.event;

import omnis.config.core.context.event.OmnisEvent;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-10 09:00
 **/
public interface ConfigInstanceEventPublisher {

    void publishEvent(OmnisEvent event);

}
