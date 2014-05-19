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

import org.kitteh.craftirc.endpoint.defaults.IRCEndpoint;
import org.kitteh.irc.Bot;
import org.kitteh.irc.EventHandler;
import org.kitteh.irc.event.ChannelCTCPEvent;
import org.kitteh.irc.event.ChannelMessageEvent;

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

    IRCBot(String name, Bot bot) {
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

    private class Listener {
        @EventHandler
        public void message(ChannelMessageEvent event) {
            // TODO
        }

        @EventHandler
        public void action(ChannelCTCPEvent event) {
            if (event.getMessage().startsWith("ACTION ")) {
                final String message = event.getMessage().substring("ACTION ".length());
                // TODO
            }
        }
    }
}