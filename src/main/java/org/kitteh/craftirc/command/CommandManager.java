/*
 * * Copyright (C) 2014-2016 Matt Baxter http://kitteh.org
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
package org.kitteh.craftirc.command;

import ninja.leaping.configurate.ConfigurationNode;
import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.command.defaults.Version;
import org.kitteh.craftirc.util.loadable.Loadable;
import org.kitteh.craftirc.util.loadable.LoadableTypeManager;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles Commands.
 */
public final class CommandManager extends LoadableTypeManager<Command> {
    private Map<String, Command> commands = new ConcurrentHashMap<>();

    public CommandManager(@Nonnull CraftIRC plugin) {
        super(plugin, Command.class);
        // Register command types here
        this.registerType(Version.class);
    }

    public void processCommand(String command, CommandContext commandContext) {

    }

    @Override
    protected void processCompleted(@Nonnull Command loaded) {
        Loadable.Type def = loaded.getClass().getAnnotation(Loadable.Type.class);
        if (this.commands.put(def.name(), loaded) != null) {
            CraftIRC.log().warning("Replacing previously loaded command \"" + def.name() + "\" for newer definition");
        }
    }

    @Override
    protected void processFailedLoad(@Nonnull Exception exception, @Nonnull ConfigurationNode data) {
        CraftIRC.log().warning("Failed to load Command", exception);
    }

    @Override
    protected void processInvalid(@Nonnull String reason, @Nonnull ConfigurationNode data) {
        CraftIRC.log().warning("Encountered invalid Command: " + reason);
    }
}