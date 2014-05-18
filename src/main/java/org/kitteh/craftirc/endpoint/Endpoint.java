package org.kitteh.craftirc.endpoint;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.loadable.Loadable;
import org.kitteh.craftirc.util.MapGetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Endpoints are the origin and destination of messages tracked by this
 * plugin.
 */
public abstract class Endpoint extends Loadable {
    public class EndpointFilterLoader {
        private EndpointFilterLoader() {
        }

        public Endpoint getEndpoint() {
            return Endpoint.this;
        }

        public void addFilter(Filter filter) {
            Endpoint.this.addFilter(filter);
        }
    }

    private String name;
    private final List<Filter> filters = new CopyOnWriteArrayList<>();

    /**
     * Gets the name of this Endpoint.
     *
     * @return the name of this endpoint
     */
    public final String getName() {
        return this.name;
    }

    private void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    /**
     * Optional method to load any additional information for this Endpoint.
     * <p/>
     * Additional information is stored under 'extra' in the Endpoint's
     * definition.
     * <p/>
     * This method is not called if no such section exists.
     *
     * @param data the 'extra' section of the configuration
     */
    protected void loadExtra(Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        // By default, nothing extra to load
    }

    protected final void load(CraftIRC plugin, Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        this.name = MapGetter.getString(data, "name");
        final Map<Object, Object> extras = MapGetter.getMap(data, "extra");
        this.loadExtra(extras == null ? new HashMap<>() : extras);

        List<?> filters = MapGetter.get(data, "filters", List.class);
        if (filters != null) {
            plugin.getFilterRegistry().loadList(filters, new EndpointFilterLoader());
        }
    }

    /**
     * Processes a received message prior to processing by filters. For
     * example, now is the place to add the list of targetted Minecraft
     * players, in a
     * {@link org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint}.
     *
     * @param message message to process
     */
    protected void preProcessReceivedMessage(TargetedMessage message) {
        // By default, don't do anything
    }

    /**
     * We get signal.
     * <p/>
     * A message received here has been processed by filters and is not
     * rejected by them.
     *
     * @param message the message to be displayed
     */
    protected abstract void receiveMessage(TargetedMessage message);

    /**
     * Receive a message and process.
     * <p/>
     * Sequence of events:
     * <ol>
     * <li>Pre-process</li>
     * <li>Run through filters, stop if rejected</li>
     * <li>Handle as received</li>
     * </ol>
     *
     * @param message the message sent by the source
     */
    final void receiveMessage(Message message) {
        TargetedMessage targetedMessage = new TargetedMessage(this, message);
        try {
            this.preProcessReceivedMessage(targetedMessage);
        } catch (Throwable thrown) {
            // TODO output stacktrace
        }
        for (Filter filter : this.filters) {
            try {
                filter.processMessage(targetedMessage);
                if (targetedMessage.isRejected()) {
                    return;
                }
            } catch (Throwable thrown) {
                // TODO output stacktrace
            }
        }
        this.receiveMessage(targetedMessage);
    }
}