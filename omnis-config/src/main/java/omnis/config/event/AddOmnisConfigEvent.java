package omnis.config.event;


import omnis.config.core.context.event.OmnisEvent;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-15 08:33
 **/
public class AddOmnisConfigEvent<T> extends OmnisEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public AddOmnisConfigEvent(T source) {
        super(source);
    }
}
