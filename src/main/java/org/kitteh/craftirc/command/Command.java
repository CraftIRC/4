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

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.loadable.Loadable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Commands can be run anywhere!
 */
public abstract class Command extends Loadable {
    private List<String> validEndpoints;

    public List<String> getValidEndpoints() {
        return this.validEndpoints;
    }

    /**
     * Loads this command's data.
     *
     * @param data information to load
     * @throws CraftIRCInvalidConfigException
     */
    protected void load(@Nonnull ConfigurationNode data) throws CraftIRCInvalidConfigException {

    }

    @Override
    protected final void load(@Nonnull CraftIRC plugin, @Nonnull ConfigurationNode data) throws CraftIRCInvalidConfigException {
        this.validEndpoints = this.getList(data.getNode("endpoints"));
        this.load(data);
    }

    private List<String> getList(ConfigurationNode node) throws CraftIRCInvalidConfigException {
        if (node.isVirtual()) {
            throw new CraftIRCInvalidConfigException("Missing list");
        }
        if (node.hasListChildren()) {
            try {
                return new ArrayList<>(node.getList(TypeToken.of(String.class)));
            } catch (ObjectMappingException e) {
                throw new CraftIRCInvalidConfigException("Could not get list: " + e.getMessage());
            }
        } else {
            List<String> list = new ArrayList<>();
            list.add(node.getString());
            return list;
        }
    }

    public abstract void execute(CommandContext context);
}