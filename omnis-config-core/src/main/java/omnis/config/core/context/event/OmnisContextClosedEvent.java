package omnis.config.core.context.event;


/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-15 08:26
 **/
public class OmnisContextClosedEvent<T> extends OmnisEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public OmnisContextClosedEvent(T source) {
        super(source);
    }
}
