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
package org.kitteh.craftirc.endpoint;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * Endpoints are the origin and destination of messages tracked by this
 * plugin.
 */
public abstract class Endpoint extends Loadable {
    public class EndpointFilterLoader {
        private EndpointFilterLoader() {
        }

        public Endpoint getEndpoint() {
            return Endpoint.this;
        }

        public void addFilter(Filter filter) {
            Endpoint.this.addFilter(filter);
        }
    }

    public static final String MESSAGE_FORMAT = "MESSAGE_FORMAT";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
    public static final String SENDER_NAME = "SENDER_NAME";

    private String name;
    private final List<Filter> filters = new CopyOnWriteArrayList<>();

    /**
     * Gets the name of this Endpoint.
     *
     * @return the name of this endpoint
     */
    public final String getName() {
        return this.name;
    }

    private void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    /**
     * Optional method to load any additional information for this Endpoint.
     * <p/>
     * Additional information is stored under 'extra' in the Endpoint's
     * definition.
     * <p/>
     * This method is not called if no such section exists.
     *
     * @param data the 'extra' section of the configuration
     */
    protected void loadExtra(Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        // By default, nothing extra to load
    }

    @Override
    protected final void load(CraftIRC plugin, Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        this.name = MapGetter.getString(data, "name");
        final Map<Object, Object> extra = MapGetter.getMap(data, "extra");
        this.loadExtra(extra == null ? new HashMap<>() : extra);

        List<?> filters = MapGetter.get(data, "filters", List.class);
        if (filters != null) {
            plugin.getFilterManager().loadList(filters, new EndpointFilterLoader());
        }
    }

    /**
     * Processes a received message prior to processing by filters. For
     * example, now is the place to add the list of targetted Minecraft
     * players, in a
     * {@link org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint}.
     *
     * @param message message to process
     */
    protected void preProcessReceivedMessage(TargetedMessage message) {
        // By default, don't do anything
    }

    /**
     * We get signal.
     * <p/>
     * A message received here has been processed by filters and is not
     * rejected by them.
     *
     * @param message the message to be displayed
     */
    protected abstract void receiveMessage(TargetedMessage message);

    /**
     * Receive a message and process.
     * <p/>
     * Sequence of events:
     * <ol>
     * <li>Pre-process</li>
     * <li>Run through filters, stop if rejected</li>
     * <li>Handle as received</li>
     * </ol>
     *
     * @param message the message sent by the source
     */
    final void receiveMessage(Message message) {
        TargetedMessage targetedMessage = new TargetedMessage(this, message);
        try {
            this.preProcessReceivedMessage(targetedMessage);
        } catch (Throwable thrown) {
            CraftIRC.log().log(Level.WARNING, "Unable to preprocess a received message", thrown);
        }
        for (Filter filter : this.filters) {
            try {
                filter.processMessage(targetedMessage);
                if (targetedMessage.isRejected()) {
                    return;
                }
            } catch (Throwable thrown) {
                CraftIRC.log().log(Level.WARNING, "Unable to process a received message", thrown);
            }
        }
        this.receiveMessage(targetedMessage);
    }
}