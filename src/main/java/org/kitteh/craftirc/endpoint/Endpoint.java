package org.kitteh.craftirc.endpoint;

import org.apache.commons.lang.Validate;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.endpoint.filter.defaults.RegexFilter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Endpoints are the origin and destination of messages tracked by this
 * plugin.
 */
public abstract class Endpoint {
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
        Validate.notNull(filter, "Cannot add null filter!");
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
    protected void loadExtra(Map<String, Object> data) throws CraftIRCInvalidConfigException {
        // By default, nothing extra to load
    }

    /**
     * Loads a named {@link org.kitteh.craftirc.endpoint.filter.Filter}.
     *
     * @param name name of the Filter to load
     * @param data associated information
     */
    protected abstract Filter loadFilter(String name, Map<?, ?> data);

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
     * Loads filters for this Endpoint.
     *
     * @param filters configuration section describing the filters to load
     */
    final void loadFilters(List<Map<?, ?>> filters) {
        for (Map<?, ?> map : filters) {
            String type = map.get("type").toString();
            try {
                Filter filter = this.loadFilter(type, map);
                if (filter == null) {
                    // Default filters here
                    if (type.equalsIgnoreCase("regex")) {
                        filter = new RegexFilter(); // TODO data!
                    }
                }
                if (filter != null) {
                    this.addFilter(filter);
                } else {
                    // TODO log unknown filter
                }
            } catch (Throwable thrown) {
                // TODO print stacktrace
            }
        }
    }

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
                filter.processIncomingMessage(targetedMessage);
                if (targetedMessage.isRejected()) {
                    return;
                }
            } catch (Throwable thrown) {
                // TODO output stacktrace
            }
        }
        this.receiveMessage(targetedMessage);
    }

    void setName(String name) {
        this.name = name;
    }
}