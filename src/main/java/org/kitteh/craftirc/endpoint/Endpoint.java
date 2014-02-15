package org.kitteh.craftirc.endpoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kitteh.craftirc.endpoint.filter.Filter;

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

    protected void addFilter(Filter filter) {
        if (filter != null) {
            this.filters.add(filter);
        }
    }

    /**
     * Optional method to load any additional information for this Endpoint.
     * <p>
     * Additional information is stored under 'extra' in the Endpoint's
     * definition.
     * <p>
     * This method is not called if no such section exists.
     *
     * @param data the 'extra' section of the configuration
     */
    protected void loadExtra(Map<String, Object> data) {
        // By default, nothing extra to load
    }

    /**
     * Loads any custom filters for this Endpoint type.
     *
     * @param filters configuration section describing the filters to load
     */
    protected abstract void loadFilters(Map<String, Object> filters);

    void setName(String name) {
        this.name = name;
    }
}