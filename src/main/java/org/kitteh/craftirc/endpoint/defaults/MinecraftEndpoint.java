/*
 * * Copyright (C) 2014 Matt Baxter http://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.craftirc.endpoint.defaults;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.Message;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.util.MinecraftPlayer;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The standard {@link org.kitteh.craftirc.endpoint.Endpoint} for minecraft
 * chat messages.
 */
@Loadable.Type(name = "minecraft")
public class MinecraftEndpoint extends Endpoint implements Listener {
    public static final String PLAYER_LIST = "RECIPIENT_NAMES";

    private final CraftIRC plugin;

    public MinecraftEndpoint(CraftIRC plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    protected void preProcessReceivedMessage(TargetedMessage message) {
        List<MinecraftPlayer> players = new LinkedList<>();
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            players.add(new MinecraftPlayer(player.getName(), player.getUniqueId()));
        }
        message.getCustomData().put(MinecraftEndpoint.PLAYER_LIST, players);
    }

    @Override
    protected void receiveMessage(TargetedMessage message) {
        @SuppressWarnings("unchecked")
        List<MinecraftPlayer> recipients = (List<MinecraftPlayer>) message.getCustomData().get(MinecraftEndpoint.PLAYER_LIST);
        for (MinecraftPlayer recipient : recipients) {
            Player player = this.plugin.getServer().getPlayerExact(recipient.getName());
            if (player != null) {
                player.sendMessage(message.getCustomMessage());
            }
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Map<String, Object> data = new HashMap<>();
        Set<MinecraftPlayer> recipients = new HashSet<>();
        for (Player player : event.getRecipients()) {
            recipients.add(new MinecraftPlayer(player.getName(), player.getUniqueId()));
        }
        String format = event.getFormat();
        String message = event.getMessage();
        String sender = event.getPlayer().getName();
        data.put(Endpoint.MESSAGE_FORMAT, format);
        data.put(Endpoint.MESSAGE_TEXT, message);
        data.put(MinecraftEndpoint.PLAYER_LIST, recipients);
        data.put(Endpoint.SENDER_NAME, sender);
        this.plugin.getEndpointManager().sendMessage(new Message(this, String.format(event.getFormat(), sender, message), data));
    }
}