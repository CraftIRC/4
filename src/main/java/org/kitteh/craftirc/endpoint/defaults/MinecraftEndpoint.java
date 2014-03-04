package org.kitteh.craftirc.endpoint.defaults;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.EndpointType;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.endpoint.filter.defaults.BukkitPermissionFilter;
import org.kitteh.craftirc.util.MinecraftPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * The standard {@link org.kitteh.craftirc.endpoint.Endpoint} for minecraft
 * chat messages.
 */
@EndpointType(name = "minecraft", sync = true)
public class MinecraftEndpoint extends Endpoint {
    public static final String PLAYER_LIST = "destinationPlayers";

    private Server server;

    public MinecraftEndpoint(Server server) {
        this.server = server;
    }

    @Override
    protected Filter loadFilter(String name, Object data) {
        if (name.equalsIgnoreCase("permission")) {
            return new BukkitPermissionFilter(this.server, data.toString());
        }
        return null;
    }

    @Override
    protected void processReceivedMessage(TargetedMessage message) {
        List<MinecraftPlayer> players = new LinkedList<MinecraftPlayer>();
        for (Player player : this.server.getOnlinePlayers()) {
            players.add(new MinecraftPlayer(player.getName(), player.getUniqueId()));
        }
        message.getCustomData().put(MinecraftEndpoint.PLAYER_LIST, players);
    }
}