package org.kitteh.craftirc.endpoint;

import org.apache.commons.lang.Validate;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.message.EndpointMessage;

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

    /**
     * Adds a filter to the loaded Endpoint.
     * <p/>
     * Filters are added to a list and processed in the order they were
     * added.
     *
     * @param filter filter to be added
     */
    protected void addFilter(Filter filter) {
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
     * Loads any custom filters for this Endpoint type.
     * <p/>
     * It is up to the implementation to call
     * {@link #addFilter(org.kitteh.craftirc.endpoint.filter.Filter)} to add
     * loaded filters.
     *
     * @param filters configuration section describing the filters to load
     */
    protected void loadFilters(Map<String, Object> filters) {
        // By default, don't load any filters
    }

    /**
     * Processes a received message prior to processing by filters. For
     * example, now is the place to add the list of targetted Minecraft
     * players, in a
     * {@link org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint}.
     *
     * @param message message to process
     */
    protected void processReceivedMessage(EndpointMessage message) {
        // By default, don't do anything
    }

    final void receiveMessage(EndpointMessage message) {
        for (Filter filter : this.filters) {
            filter.processIncomingMessage(message);
        }
    }

    void setName(String name) {
        this.name = name;
    }
}