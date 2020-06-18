package omnis.config.client.event;

import omnis.config.client.context.ConfigClientContext;
import omnis.config.core.context.event.OmnisEvent;

/**
 * @description:
 * @author: haochencheng
 * @create: 2020-06-18 08:56
 **/
public class ConfigContextStartedEvent extends OmnisEvent<ConfigClientContext> {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ConfigContextStartedEvent(ConfigClientContext source) {
        super(source);
    }
}
