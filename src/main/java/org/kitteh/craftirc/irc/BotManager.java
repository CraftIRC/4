package org.kitteh.craftirc.irc;

import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.Pair;
import org.kitteh.irc.BotBuilder;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages IRC bots.
 */
public final class BotManager {
    private Map<String, IRCBot> bots = new ConcurrentHashMap<>();

    public BotManager(List<?> bots) {
        this.loadBots(bots);
    }

    public IRCBot getBot(String name) {
        return this.bots.get(name);
    }

    private void loadBots(List<?> list) {
        Set<String> usedBotNames = new HashSet<>();
        for (final Object listElement : list) {
            final Map<?, ?> data;
            if ((data = MapGetter.castToMap(listElement)) == null) {
                // TODO: Track (Don't fire each time!) that an invalid entry was added
                continue;
            }
            final String name = MapGetter.getString(data, "name");
            if (name == null) {
                // TODO fire message for unnamed/invalidly-named bot
                continue;
            }
            if (usedBotNames.contains(name)) {
                // TODO fire message for duplicate endpoint name 'name'
                continue;
            }
            usedBotNames.add(name);

            String nick = MapGetter.getString(data, "nick");
            String server = MapGetter.getString(data, "host");
            Integer port = MapGetter.getInt(data, "port");
            String user = MapGetter.getString(data, "user");
            String realname = MapGetter.getString(data, "realname");
            Map<?,?> bindMap = MapGetter.get(data, "bind", Map.class);
            String bindhost = MapGetter.getString(bindMap, "host");
            Integer bindport = MapGetter.getInt(bindMap, "port");
            BotBuilder botBuilder = new BotBuilder(name);
            botBuilder.server(server != null ? server : "localhost");
            botBuilder.server(port != null ? port : 6667);
            botBuilder.user(user != null ? user : "CraftIRC");
            botBuilder.realName(realname != null ? realname : "CraftIRC Bot");
            if (bindhost != null) {
                botBuilder.bind(bindhost);
            }
            botBuilder.bind(bindport!= null ? bindport : 0);
            botBuilder.nick(nick != null ? nick : "CraftIRC");

            bots.put(name, new IRCBot(name, botBuilder.build()));
        }
    }
}