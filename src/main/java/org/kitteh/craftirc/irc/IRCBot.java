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
package org.kitteh.craftirc.irc;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.Message;
import org.kitteh.craftirc.endpoint.defaults.IRCEndpoint;
import org.kitteh.irc.Bot;
import org.kitteh.irc.EventHandler;
import org.kitteh.irc.elements.Actor;
import org.kitteh.irc.elements.Channel;
import org.kitteh.irc.elements.User;
import org.kitteh.irc.event.ChannelCTCPEvent;
import org.kitteh.irc.event.ChannelMessageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Wraps an IRC bot and handles events
 */
public final class IRCBot {
    private final Bot bot;
    private final String name;
    private final Map<String, Set<IRCEndpoint>> channels = new ConcurrentHashMap<>();
    private final CraftIRC plugin;

    IRCBot(CraftIRC plugin, String name, Bot bot) {
        this.plugin = plugin;
        this.bot = bot;
        this.name = name;
        this.bot.getEventManager().registerEventListener(new Listener());
    }

    public String getName() {
        return this.name;
    }

    public void addChannel(IRCEndpoint endpoint, String channel) {
        this.bot.addChannel(channel);
        Set<IRCEndpoint> points = this.channels.get(channel.toLowerCase());
        if (points == null) {
            points = new CopyOnWriteArraySet<>();
            this.channels.put(channel.toLowerCase(), points);
        }
        points.add(endpoint);
    }

    public void sendMessage(String target, String message) {
        this.bot.sendMessage(target, message);
    }

    void shutdown() {
        this.bot.shutdown("CraftIRC! http://dev.bukkit.org/bukkit-plugins/craftirc");
    }

    private void sendMessage(User sender, Channel channel, String message, IRCEndpoint.MessageType messageType) {
        if (!this.channels.containsKey(channel.getName())) {
            return;
        }
        for (IRCEndpoint endpoint : this.channels.get(channel.getName())) {
            Map<String, Object> data = new HashMap<>();
            data.put(IRCEndpoint.IRC_CHANNEL, channel.getName());
            data.put(IRCEndpoint.IRC_MASK, sender.getName());
            data.put(IRCEndpoint.IRC_MESSAGE_TYPE, messageType);
            data.put(IRCEndpoint.IRC_NICK, sender.getNick());
            data.put(Endpoint.MESSAGE_FORMAT, messageType.getFormat());
            data.put(Endpoint.MESSAGE_TEXT, message);
            this.plugin.getEndpointManager().sendMessage(new Message(endpoint, String.format(messageType.getFormat(), sender.getNick()), data));
        }
    }

    private class Listener {
        @EventHandler
        public void message(ChannelMessageEvent event) {
            Actor actor = event.getSender();
            if (actor instanceof User) {
                IRCBot.this.sendMessage((User) actor, event.getChannel(), event.getMessage(), IRCEndpoint.MessageType.MESSAGE);
            }
        }

        @EventHandler
        public void action(ChannelCTCPEvent event) {
            if (event.getMessage().startsWith("ACTION ") && event.getSender() instanceof User) {
                IRCBot.this.sendMessage((User) event.getSender(), event.getChannel(), event.getMessage().substring("ACTION ".length()), IRCEndpoint.MessageType.ME);
            }
        }
    }
}