package org.kitteh.craftirc.endpoint.filter.defaults;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.MinecraftPlayer;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A filter by permission node.
 */
@Loadable.Type(name = "bukkit-permission")
public final class BukkitPermissionFilter extends Filter {
    private String permission;
    private final Server server;

    public BukkitPermissionFilter(Server server) {
        this.server = server;
    }

    @Override
    protected void load(Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        if ((this.permission = MapGetter.getString(data, "permission")) == null) {
            throw new CraftIRCInvalidConfigException("Invalid AntiHighlight config. Requires 'permission' defined");
        }
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
    public void processMessage(TargetedMessage message) {
        if (message.getCustomData().containsKey(MinecraftEndpoint.PLAYER_LIST)) {
            @SuppressWarnings("unchecked")
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