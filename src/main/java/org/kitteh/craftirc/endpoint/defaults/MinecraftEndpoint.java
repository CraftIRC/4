package org.kitteh.craftirc.endpoint.defaults;

import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.EndpointType;
import org.kitteh.craftirc.endpoint.filter.defaults.BukkitPermissionFilter;
import org.kitteh.craftirc.message.EndpointMessage;
import org.kitteh.craftirc.util.MinecraftPlayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The standard {@link org.kitteh.craftirc.endpoint.Endpoint} for minecraft
 * chat messages.
 */
@EndpointType(name = "minecraft")
public class MinecraftEndpoint extends Endpoint {
    public static final String PLAYER_LIST = "destinationPlayers";

    @Override
    protected void loadFilters(Map<String, Object> filters) {
        if (filters.containsKey("permission")) {
            this.addFilter(new BukkitPermissionFilter(filters.get("permission").toString()));
        }
    }

    @Override
    protected void processReceivedMessage(EndpointMessage message) {
        // TODO populate list
        List<MinecraftPlayer> players = new LinkedList<MinecraftPlayer>();
        message.getCustomData().put(MinecraftEndpoint.PLAYER_LIST, players);
    }
}