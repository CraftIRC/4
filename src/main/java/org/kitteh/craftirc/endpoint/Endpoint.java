package org.kitteh.craftirc.endpoint;

import org.apache.commons.lang.Validate;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.endpoint.filter.defaults.RegexFilter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Endpoints are the origin and destination of messages tracked by this
 * plugin.
 */
public abstract class Endpoint {
    private String name;
    private List<Filter> filters = new CopyOnWriteArrayList<Filter>();

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
    protected void loadExtra(Map<String, Object> data) {
        // By default, nothing extra to load
    }

    /**
     * Loads a named {@link org.kitteh.craftirc.endpoint.filter.Filter}.
     *
     * @param name name of the Filter to load
     * @param data associated information
     */
    protected abstract Filter loadFilter(String name, Object data);

    /**
     * Processes a received message prior to processing by filters. For
     * example, now is the place to add the list of targetted Minecraft
     * players, in a
     * {@link org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint}.
     *
     * @param message message to process
     */
    protected void processReceivedMessage(TargetedMessage message) {
        // By default, don't do anything
    }

    /**
     * Loads filters for this Endpoint.
     *
     * @param filters configuration section describing the filters to load
     */
    final void loadFilters(Map<String, Object> filters) {
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            try {
                Filter filter = this.loadFilter(entry.getKey(), entry.getValue());
                if (filter == null) {
                    // Default filters here
                    if (entry.getKey().equalsIgnoreCase("regex")) {
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

    final void receiveMessage(Message message) {
        TargetedMessage targetedMessage = new TargetedMessage(this, message);
        try {
            this.processReceivedMessage(targetedMessage);
        } catch (Throwable thrown) {
            // TODO output stacktrace
        }
        for (Filter filter : this.filters) {
            try {
                filter.processIncomingMessage(targetedMessage);
            } catch (Throwable thrown) {
                // TODO output stacktrace
            }
        }
    }

    void setName(String name) {
        this.name = name;
    }
}