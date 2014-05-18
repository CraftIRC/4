package org.kitteh.craftirc.irc;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.irc.BotBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages IRC bots.
 */
public final class BotManager {
    private final Map<String, IRCBot> bots = new ConcurrentHashMap<>();

    public BotManager(List<?> bots) {
        this.loadBots(bots);
    }

    public IRCBot getBot(String name) {
        return this.bots.get(name);
    }

    private void loadBots(List<?> list) {
        Set<String> usedBotNames = new HashSet<>();
        int nonMap = 0;
        int noName = 0;
        for (final Object listElement : list) {
            final Map<Object, Object> data;
            if ((data = MapGetter.castToMap(listElement)) == null) {
                nonMap++;
                continue;
            }
            final String name = MapGetter.getString(data, "name");
            if (name == null) {
                CraftIRC.log().warning("");
                noName++;
                continue;
            }
            if (usedBotNames.contains(name)) {
                CraftIRC.log().warning(String.format("Ignoring duplicate bot name %s", name));
                continue;
            }
            usedBotNames.add(name);

            String nick = MapGetter.getString(data, "nick");
            String server = MapGetter.getString(data, "host");
            Integer port = MapGetter.getInt(data, "port");
            String user = MapGetter.getString(data, "user");
            String realname = MapGetter.getString(data, "realname");
            Map<Object, Object> bindMap = MapGetter.getMap(data, "bind");
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
            botBuilder.bind(bindport != null ? bindport : 0);
            botBuilder.nick(nick != null ? nick : "CraftIRC");

            bots.put(name, new IRCBot(name, botBuilder.build()));
        }
        if (nonMap > 0) {
            CraftIRC.log().warning(String.format("Bots list contained %d entries which were not maps", nonMap));
        }
        if (noName > 0) {
            CraftIRC.log().warning(String.format("Bots list contained %d entries without a 'name'", noName));
        }
    }
}