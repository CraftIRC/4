package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.message.EndpointMessage;
import org.kitteh.craftirc.util.MinecraftPlayer;

import java.util.List;

/**
 * A filter by permission node.
 */
public final class BukkitPermissionFilter implements Filter {
    private String permission;

    /**
     * Creates a filter by permission node.
     *
     * @param permission permission node to by which to filter
     */
    public BukkitPermissionFilter(String permission) {
        this.permission = permission;
    }

    /**
     * Gets the permission node being monitored.
     *
     * @return the permission node monitored
     */
    public String getPermission() {
        return this.permission;
    }

    @Override
    public void processIncomingMessage(EndpointMessage message) {
        if (message.getCustomData().containsKey(MinecraftEndpoint.PLAYER_LIST)) {
            List<MinecraftPlayer> players = (List<MinecraftPlayer>) message.getCustomData().get(MinecraftEndpoint.PLAYER_LIST);
            // TODO determine if player has the permission and remove otherwise
        }
    }
}