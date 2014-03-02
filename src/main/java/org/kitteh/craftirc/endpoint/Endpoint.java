package org.kitteh.craftirc.endpoint;

import org.kitteh.craftirc.endpoint.filter.Filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Endpoints are the origin and destination of messages tracked by this
 * plugin.
 */
public abstract class Endpoint {
    private String name;
    private List<Filter> filters = new LinkedList<Filter>();

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
        if (filter != null) {
            this.filters.add(filter);
        }
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
    protected abstract void loadFilters(Map<String, Object> filters);

    void setName(String name) {
        this.name = name;
    }
}