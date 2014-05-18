package org.kitteh.craftirc.endpoint;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.defaults.IRCEndpoint;
import org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.loadable.LoadableTypeManager;
import org.kitteh.craftirc.util.MapGetter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maintains {@link Endpoint}s and classes corresponding to Endpoint types.
 */
public final class EndpointManager extends LoadableTypeManager<Endpoint> {
    private final Map<String, Endpoint> endpoints = new ConcurrentHashMap<>();
    private final Map<String, List<String>> links = new ConcurrentHashMap<>();
    private final MessageDistributor messageDistributor;

    public EndpointManager(CraftIRC plugin, List<?> endpoints, List<?> links) {
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
    protected void processFailedLoad(Exception exception, Map<?, ?> data) {

    }

    @Override
    protected void processInvalid(String reason, Map<?, ?> data) {

    }

    private void loadLinks(List<?> list) {
        if (list == null) {
            // TODO fire message for lack of links
            return;
        }
        for (final Object listElement : list) {
            final Map<?, ?> linkMap;
            if ((linkMap = MapGetter.castToMap(listElement)) == null) {
                // TODO: Track (Don't fire each time!) that an invalid entry was added
                continue;
            }
            final String source = MapGetter.getString(linkMap, "source");
            if (source == null) {
                // TODO fire message for link without source
                continue;
            }
            final String target = MapGetter.getString(linkMap, "target");
            if (target == null) {
                // TODO fire message for link without target
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
    }

    private void addLink(String source, String target) {
        source = source.toLowerCase();
        target = target.toLowerCase();
        List<String> targets = this.links.get(source);
        if (targets == null) {
            targets = new LinkedList<>();
            this.links.put(source, targets);
        }
        targets.add(target);
    }
}