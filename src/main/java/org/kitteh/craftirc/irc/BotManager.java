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
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.shutdownable.Shutdownable;
import org.kitteh.irc.client.library.ClientBuilder;

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
    private final CraftIRC plugin;

    public BotManager(CraftIRC plugin, List<Object> bots) {
        this.plugin = plugin;
        this.plugin.trackShutdownable(new Shutdownable() {
            @Override
            public void shutdown() {
                BotManager.this.bots.values().forEach(IRCBot::shutdown);
            }
        });
        this.loadBots(bots);
    }

    public IRCBot getBot(String name) {
        return this.bots.get(name);
    }

    private void loadBots(List<Object> list) {
        Set<String> usedBotNames = new HashSet<>();
        int nonMap = 0;
        int noName = 0;
        for (final Object listElement : list) {
            final Map<Object, Object> data = MapGetter.castToMap(listElement);
            if (data == null) {
                nonMap++;
                continue;
            }
            final String name = MapGetter.getString(data, "name");
            if (name == null) {
                noName++;
                continue;
            }
            if (!usedBotNames.add(name)) {
                CraftIRC.log().warning(String.format("Ignoring duplicate bot with name %s", name));
                continue;
            }

            this.addBot(name, data);
        }
        if (nonMap > 0) {
            CraftIRC.log().warning(String.format("Bots list contained %d entries which were not maps", nonMap));
        }
        if (noName > 0) {
            CraftIRC.log().warning(String.format("Bots list contained %d entries without a 'name'", noName));
        }
    }

    private void addBot(String name, Map<Object, Object> data) {
        String nick = MapGetter.getString(data, "nick");
        String server = MapGetter.getString(data, "host");
        Integer port = MapGetter.getInt(data, "port");
        String user = MapGetter.getString(data, "user");
        String realname = MapGetter.getString(data, "realname");
        Map<Object, Object> bindMap = MapGetter.getMap(data, "bind");
        String bindhost = MapGetter.getString(bindMap, "host");
        Integer bindport = MapGetter.getInt(bindMap, "port");
        ClientBuilder botBuilder = new ClientBuilder();
        botBuilder.name(name);
        botBuilder.server(server != null ? server : "localhost");
        botBuilder.server(port != null ? port : 6667);
        botBuilder.user(user != null ? user : "CraftIRC");
        botBuilder.realName(realname != null ? realname : "CraftIRC Bot");
        if (bindhost != null) {
            botBuilder.bind(bindhost);
        }
        botBuilder.bind(bindport != null ? bindport : 0);
        botBuilder.nick(nick != null ? nick : "CraftIRC");

        this.bots.put(name, new IRCBot(this.plugin, name, botBuilder.build()));
    }
}