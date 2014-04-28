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
import java.util.Map;

/**
 * The standard {@link org.kitteh.craftirc.endpoint.Endpoint} for minecraft
 * chat messages.
 */
@EndpointType(name = "minecraft", sync = true)
public class MinecraftEndpoint extends Endpoint {
    public static final String PLAYER_LIST = "destinationPlayers";

    private final Server server;

    public MinecraftEndpoint(Server server) {
        this.server = server;
    }

    @Override
    protected Filter loadFilter(String name, Map<?, ?> data) {
        if (name.equalsIgnoreCase("permission")) {
            Object oPerm = data.get("permission");
            if (oPerm instanceof String) {
                return new BukkitPermissionFilter(this.server, (String) oPerm);
            }
        }
        return null;
    }

    @Override
    protected void preProcessReceivedMessage(TargetedMessage message) {
        List<MinecraftPlayer> players = new LinkedList<>();
        for (Player player : this.server.getOnlinePlayers()) {
            players.add(new MinecraftPlayer(player.getName(), player.getUniqueId()));
        }
        message.getCustomData().put(MinecraftEndpoint.PLAYER_LIST, players);
    }

    @Override
    protected void receiveMessage(TargetedMessage message) {
        @SuppressWarnings("unchecked")
        List<MinecraftPlayer> recipients = (List<MinecraftPlayer>) message.getCustomData().get(MinecraftEndpoint.PLAYER_LIST);
        for (MinecraftPlayer recipient : recipients) {
            Player player = this.server.getPlayerExact(recipient.getName());
            if (player != null) {
                player.sendMessage(message.getCustomMessage());
            }
        }
    }
}