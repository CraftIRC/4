package org.kitteh.craftirc.endpoint.defaults;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.util.MinecraftPlayer;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.LinkedList;
import java.util.List;

/**
 * The standard {@link org.kitteh.craftirc.endpoint.Endpoint} for minecraft
 * chat messages.
 */
@Loadable.Type(name = "minecraft")
public class MinecraftEndpoint extends Endpoint {
    public static final String PLAYER_LIST = "destinationPlayers";

    private final Server server;

    public MinecraftEndpoint(Server server) {
        this.server = server;
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