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
    private final Listener listener;
    private final String name;
    private final Map<String, Set<IRCEndpoint>> channels = new ConcurrentHashMap<>();

    IRCBot(String name, Bot bot) {
        this.bot = bot;
        this.listener = new Listener();
        this.name = name;
        this.bot.getEventManager().registerEventListener(this.listener);
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
            if (event.getMessage().startsWith("ACTION ")){
                final String message = event.getMessage().substring("ACTION ".length());
                // TODO
            }
        }
    }
}