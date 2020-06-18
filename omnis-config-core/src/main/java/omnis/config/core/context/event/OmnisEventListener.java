package omnis.config.core.context.event;


import java.util.EventListener;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-15 08:20
 **/
@FunctionalInterface
public interface OmnisEventListener<T extends OmnisEvent> extends EventListener {

    void onOmnisEvent(T event);

}
