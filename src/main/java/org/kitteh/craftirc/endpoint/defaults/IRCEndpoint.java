package org.kitteh.craftirc.endpoint.defaults;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.EndpointType;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.irc.IRCBot;
import org.kitteh.craftirc.util.MapGetter;

import java.util.Map;

/**
 * The standard {@link org.kitteh.craftirc.endpoint.Endpoint} for IRC bots.
 */
@EndpointType(name = "irc")
public class IRCEndpoint extends Endpoint {
    private IRCBot bot;
    private final CraftIRC plugin;

    public IRCEndpoint(CraftIRC plugin) {
        this.plugin = plugin;
    }

    @Override
    protected Filter loadFilter(String name, Map<?, ?> data) {
        return null;
    }

    @Override
    protected void receiveMessage(TargetedMessage message) {

    }

    @Override
    protected void loadExtra(Map<String, Object> data) throws CraftIRCInvalidConfigException {
        final String botName = MapGetter.getString(data, "bot");
        if (botName == null || (this.bot = this.plugin.getBotManager().getBot(botName)) == null) {
            throw new CraftIRCInvalidConfigException("Invalid bot name '"+botName+"'");
        }
        final String channel = MapGetter.getString(data, "channel");
        if (channel == null) {
            throw new CraftIRCInvalidConfigException("No channel defined");
        }
        bot.addChannel(this, channel);
    }
}