/*
 * * Copyright (C) 2014-2015 Matt Baxter http://kitteh.org
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
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.ClientBuilder;
import org.kitteh.irc.client.library.auth.protocol.NickServ;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    /**
     * Initialized by {@link CraftIRC} main.
     *
     * @param plugin the CraftIRC instance
     * @param bots list of bot data to load
     */
    public BotManager(@Nonnull CraftIRC plugin, @Nonnull List<Object> bots) {
        this.plugin = plugin;
        this.plugin.trackShutdownable(new Shutdownable() {
            @Override
            public void shutdown() {
                BotManager.this.bots.values().forEach(IRCBot::shutdown);
            }
        });
        this.loadBots(bots);
    }

    /**
     * Gets a bot by name.
     *
     * @param name bot name
     * @return named bot or null if no such bot exists
     */
    @Nullable
    public IRCBot getBot(@Nonnull String name) {
        return this.bots.get(name);
    }

    private void loadBots(@Nonnull List<Object> list) {
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

    private void addBot(@Nonnull String name, @Nonnull Map<Object, Object> data) {
        String nick = MapGetter.getString(data, "nick");
        String server = MapGetter.getString(data, "host");
        Integer port = MapGetter.getInt(data, "port");
        Boolean ssl = MapGetter.getBoolean(data, "ssl");
        String user = MapGetter.getString(data, "user");
        String realname = MapGetter.getString(data, "realname");
        String password = MapGetter.getString(data, "password");

        Map<Object, Object> bindMap = MapGetter.getMap(data, "bind");
        String bindhost = MapGetter.getString(bindMap, "host");
        Integer bindport = MapGetter.getInt(bindMap, "port");
        ClientBuilder botBuilder = Client.builder();
        botBuilder.name(name);
        botBuilder.serverHost(server != null ? server : "localhost");
        botBuilder.serverPort(port != null ? port : 6667);
        botBuilder.secure(ssl != null ? ssl : false);
        if (password != null) {
            botBuilder.serverPassword(password);
        }
        botBuilder.user(user != null ? user : "CraftIRC");
        botBuilder.realName(realname != null ? realname : "CraftIRC Bot");
        if (bindhost != null) {
            botBuilder.bindHost(bindhost);
        }
        botBuilder.bindPort(bindport != null ? bindport : 0);
        botBuilder.nick(nick != null ? nick : "CraftIRC");

        Map<Object, Object> authMap = MapGetter.getMap(data, "auth");
        if (authMap != null) {
            String authUser = MapGetter.getString(authMap, "user");
            String authPass = MapGetter.getString(authMap, "pass");
            if (authUser != null && authPass != null) {
                botBuilder.afterBuildConsumer(client -> client.getAuthManager().addProtocol(new NickServ(client, authUser, authPass)));
            }
        }

        this.bots.put(name, new IRCBot(this.plugin, name, botBuilder.build()));
    }
}