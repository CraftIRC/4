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
import org.kitteh.craftirc.endpoint.defaults.IRCEndpoint;
import org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.loadable.LoadableTypeManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Maintains {@link Endpoint}s and classes corresponding to Endpoint types.
 */
public final class EndpointManager extends LoadableTypeManager<Endpoint> {
    private final Map<String, Endpoint> endpoints = new ConcurrentHashMap<>();
    private final Map<String, List<String>> links = new ConcurrentHashMap<>();
    private final MessageDistributor messageDistributor;

    public EndpointManager(CraftIRC plugin, List<Object> endpoints, List<Object> links) {
        super(plugin, Endpoint.class);
        this.messageDistributor = new MessageDistributor(this, plugin);
        // We register ours first.
        this.registerType(MinecraftEndpoint.class);
        this.registerType(IRCEndpoint.class);

        this.loadList(endpoints);
        this.loadLinks(links);
    }

    public void sendMessage(Message message) {
        this.messageDistributor.addMessage(message);
    }

    /**
     * Gets the Endpoint destinations of a named source Endpoint
     *
     * @param source source Endpoint
     * @return destinations of a message send by the speciified Endpoint
     */
    Set<Endpoint> getDestinations(String source) {
        Set<Endpoint> destinations = new HashSet<>();
        List<String> targets = this.links.get(source);
        if (targets != null) {
            for (String target : targets) {
                Endpoint endpoint = this.endpoints.get(target);
                if (endpoint != null) {
                    destinations.add(endpoint);
                }
            }
        }
        return destinations;
    }

    @Override
    protected void processCompleted(Endpoint endpoint) throws CraftIRCInvalidConfigException {
        final String name = endpoint.getName();
        if (this.endpoints.containsKey(name)) {
            throw new CraftIRCInvalidConfigException("Duplicate Endpoint name '" + name + "'");
        }
        this.endpoints.put(name, endpoint);
    }

    @Override
    protected void processFailedLoad(Exception exception, Map<Object, Object> data) {
        CraftIRC.log().log(Level.WARNING, "Failed to load Endpoint", exception);
    }

    @Override
    protected void processInvalid(String reason, Map<Object, Object> data) {
        CraftIRC.log().warning("Encountered invalid Endpoint: " + reason);
    }

    private void loadLinks(List<Object> list) {
        int nonMap = 0;
        int noSource = 0;
        int noTarget = 0;
        for (final Object listElement : list) {
            final Map<Object, Object> linkMap;
            if ((linkMap = MapGetter.castToMap(listElement)) == null) {
                nonMap++;
                continue;
            }
            final String source = MapGetter.getString(linkMap, "source");
            if (source == null) {
                noSource++;
                continue;
            }
            final String target = MapGetter.getString(linkMap, "target");
            if (target == null) {
                noTarget++;
                continue;
            }
            final Object bidirectionalObject = linkMap.get("bidirectional");
            boolean bidirectional = false;
            if (bidirectionalObject != null) {
                if (bidirectionalObject instanceof Boolean && bidirectionalObject == Boolean.TRUE) {
                    bidirectional = true;
                } else if (bidirectionalObject instanceof String && ((String) bidirectionalObject).equalsIgnoreCase("true")) {
                    bidirectional = true;
                }
            }
            this.addLink(source, target);
            if (bidirectional) {
                this.addLink(target, source);
            }
        }
        if (nonMap > 0) {
            CraftIRC.log().warning(String.format("Links list contained %d entries which were not maps", nonMap));
        }
        if (noSource > 0) {
            CraftIRC.log().warning(String.format("Links list contained %d entries without a source specified", noSource));
        }
        if (noTarget > 0) {
            CraftIRC.log().warning(String.format("Links list contained %d entries without a target specified", noTarget));
        }
        if (this.links.isEmpty()) {
            CraftIRC.log().severe("Loaded no links! Nothing will be passed between any Endpoints!");
        }
    }

    private void addLink(String source, String target) {
        List<String> targets = this.links.get(source);
        if (targets == null) {
            targets = new LinkedList<>();
            this.links.put(source, targets);
        }
        targets.add(target);
    }
}