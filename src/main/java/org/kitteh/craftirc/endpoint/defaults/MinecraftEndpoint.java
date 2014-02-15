package org.kitteh.craftirc.endpoint.defaults;

import java.util.Map;

import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.EndpointType;
import org.kitteh.craftirc.endpoint.filter.defaults.BukkitPermissionFilter;

@EndpointType(name = "minecraft")
public class MinecraftEndpoint extends Endpoint {
    @Override
    protected void loadFilters(Map<String, Object> filters) {
        if (filters.containsKey("permission")) {
            this.addFilter(new BukkitPermissionFilter(filters.get("permission").toString()));
        }
    }
}