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