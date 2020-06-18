package omnis.config.core.context.event;

import java.util.EventObject;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-14 22:42
 **/
public abstract class OmnisEvent<T> extends EventObject {


    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public OmnisEvent(T source) {
        super(source);
    }
}
