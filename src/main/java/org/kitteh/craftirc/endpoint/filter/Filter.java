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
package org.kitteh.craftirc.endpoint.filter;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.Map;

/**
 * This is a filter.
 */
public abstract class Filter extends Loadable {
    private Endpoint endpoint;
    private Endpoint.EndpointFilterLoader loader;

    /**
     * Gets the Endpoint using this Filter instance.
     *
     * @return the Endpoint in use
     */
    protected Endpoint getEndpoint() {
        return this.endpoint;
    }

    Endpoint.EndpointFilterLoader getLoader() {
        return this.loader;
    }

    /**
     * Processes an incoming message. Should be capable of handling calls
     * from multiple threads at once.
     *
     * @param message message to process
     */
    public abstract void processMessage(TargetedMessage message);

    @Override
    protected final void load(CraftIRC plugin, Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        if (data.containsKey(FilterRegistry.Target.EndpointLoader)) {
            this.loader = (Endpoint.EndpointFilterLoader) data.get(FilterRegistry.Target.EndpointLoader);
            this.endpoint = this.loader.getEndpoint();
        }
        this.load(data);
    }

    /**
     * Loads this filter's data.
     *
     * @param data information to load
     * @throws CraftIRCInvalidConfigException
     */
    protected void load(Map<Object, Object> data) throws CraftIRCInvalidConfigException {

    }
}