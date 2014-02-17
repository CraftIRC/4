package org.kitteh.craftirc.endpoint;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.Validate;
import org.kitteh.craftirc.endpoint.defaults.MinecraftEndpoint;
import org.kitteh.craftirc.util.Pair;

/**
 * Maintains {@link Endpoint}s and classes corresponding to Endpoint types.
 */
public final class EndpointManager {
    private final Map<String, Constructor<? extends Endpoint>> types = new ConcurrentHashMap<String, Constructor<? extends Endpoint>>();
    private final Map<String, List<Pair<String, Map<?, ?>>>> unRegistered = new ConcurrentHashMap<String, List<Pair<String, Map<?, ?>>>>();
    private final Map<String, Endpoint> endpoints = new ConcurrentHashMap<String, Endpoint>();

    EndpointManager() {
        // We register ours first.
        registerEndpointType(MinecraftEndpoint.class);
    }

    /**
     * Registers an Endpoint type by {@link EndpointType} name. Endpoint
     * types registered here can be processed for loading from configuration.
     * <p>
     * Classes must have a public, no-args constructor.
     * <p>
     * Names are unique and may not be registered twice.
     *
     * @param clazz class of the Endpoint type to be registered
     */
    public void registerEndpointType(Class<? extends Endpoint> clazz) {
        Validate.isTrue(Endpoint.class.isAssignableFrom(clazz), "Submitted class '" + clazz.getSimpleName() + "' is not of type Endpoint");
        Constructor<? extends Endpoint> constructor = null;
        try {
            constructor = clazz.getConstructor();
        } catch (final Exception e) {
        }
        Validate.notNull(constructor, "Class '" + clazz.getSimpleName() + "' lacks a no-args constructor");
        final EndpointType type = clazz.getAnnotation(EndpointType.class);
        Validate.notNull(type, "Submitted class '" + clazz.getSimpleName() + "' has no EndpointType annotation");
        final String name = type.name();
        Validate.isTrue(!this.types.containsKey(name), "Endpoint type name '" + name + "' is already registered to '" + this.types.get(name).getDeclaringClass().getSimpleName() + "' and cannot be registered by '" + clazz.getSimpleName() + "'");
        this.types.put(name, constructor);
        if (this.unRegistered.containsKey(name)) {
            for (final Pair<String, Map<?, ?>> endpoint : this.unRegistered.get(name)) {
                this.loadEndpoint(constructor, endpoint.getA(), endpoint.getB());
            }
        }
    }

    private <T> T get(Map<?, ?> map, String key, Class<T> clazz) {
        final Object object = map.get(key);
        if (clazz.isAssignableFrom(object.getClass())) {
            @SuppressWarnings("unchecked")
            final T t = (T) object;
            return t;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getStringObjectMap(Object o, String name) {
        if (o == null) {
            return null;
        }
        if (!(o instanceof Map)) {
            // TODO warn about not being a map
            return null;
        }
        final Map<?, ?> map = (Map<?, ?>) o;
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                // TODO warn about not being a map of string keys
                return null;
            }
        }
        return (Map<String, Object>) map;
    }

    private void loadEndpoint(Constructor<? extends Endpoint> type, String name, Map<?, ?> map) {
        try {
            final Endpoint endpoint = type.newInstance();
            endpoint.setName(name);
            final Map<String, Object> filters = this.getStringObjectMap(map.get("filter"), "filter");
            if (filters != null) {
                endpoint.loadFilters(filters);
            }
            final Map<String, Object> extras = this.getStringObjectMap(map.get("extra"), "extra");
            if (extras != null) {
                endpoint.loadExtra(extras);
            }
            final Endpoint replaced = this.endpoints.put(name, endpoint);
            if (replaced != null) {
                // TODO message about replacing an endpoint. Alternately, don't replace.
            }
        } catch (final Exception e) {
            // TODO error loading endpoint 
        }
    }

    void loadEndpoints(List<?> list) {
        if (list == null) {
            // TODO fire message for lack of endpoints
            return;
        }
        for (final Object listElement : list) {
            if (!Map.class.isAssignableFrom(listElement.getClass())) {
                // TODO: Track (Don't fire each time!) that an invalid entry was added
                continue;
            }
            final Map<?, ?> endpointMap = (Map<?, ?>) listElement;
            final String name = this.get(endpointMap, "name", String.class);
            if (name == null) {
                // TODO fire message for unnamed/invalidly-named endpoint
                continue;
            }
            final String type = this.get(endpointMap, "type", String.class);
            if (type == null) {
                // TODO fire message for invalid type for endpoint 'name'
                continue;
            }
            final Constructor<? extends Endpoint> constructor = this.types.get(type);
            if (constructor == null) {
                List<Pair<String, Map<?, ?>>> unregged = this.unRegistered.get(type);
                if (unregged == null) {
                    unregged = new LinkedList<Pair<String, Map<?, ?>>>();
                    this.unRegistered.put(type, unregged);
                }
                unregged.add(new Pair<String, Map<?, ?>>(name, endpointMap));
                continue; // Hold for later!
            }
            this.loadEndpoint(constructor, name, endpointMap);
        }
    }
}