package org.kitteh.craftirc.endpoint.filter.defaults;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.message.EndpointMessage;
import org.kitteh.craftirc.util.MinecraftPlayer;

import java.util.Iterator;
import java.util.List;

/**
 * A filter by permission node.
 */
public final class BukkitPermissionFilter implements Filter {
    private String permission;
    private Server server;

    /**
     * Creates a filter by permission node.
     *
     * @param permission permission node to by which to filter
     */
    public BukkitPermissionFilter(Server server, String permission) {
        this.permission = permission;
        this.server = server;
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
            Iterator<MinecraftPlayer> iterator = players.iterator();
            while (iterator.hasNext()) {
                MinecraftPlayer minecraftPlayer = iterator.next();
                Player player = this.server.getPlayerExact(minecraftPlayer.getName());
                if (player == null || !player.hasPermission(this.getPermission())) {
                    iterator.remove();
                }
            }
        }
    }
}