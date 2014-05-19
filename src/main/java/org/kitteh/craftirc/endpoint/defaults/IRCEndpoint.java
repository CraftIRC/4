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

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.irc.IRCBot;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.Map;

/**
 * The standard {@link org.kitteh.craftirc.endpoint.Endpoint} for IRC bots.
 */
@Loadable.Type(name = "irc")
public class IRCEndpoint extends Endpoint {
    private IRCBot bot;
    private String channel;
    private final CraftIRC plugin;

    public IRCEndpoint(CraftIRC plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void receiveMessage(TargetedMessage message) {
        this.bot.sendMessage(this.channel, message.getCustomMessage());
    }

    @Override
    protected void loadExtra(Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        final String botName = MapGetter.getString(data, "bot");
        if (botName == null || (this.bot = this.plugin.getBotManager().getBot(botName)) == null) {
            throw new CraftIRCInvalidConfigException("Invalid bot name '" + botName + "'");
        }
        this.channel = MapGetter.getString(data, "channel");
        if (this.channel == null) {
            throw new CraftIRCInvalidConfigException("No channel defined");
        }
        this.bot.addChannel(this, this.channel);
    }
}