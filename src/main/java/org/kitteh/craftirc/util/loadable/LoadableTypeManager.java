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
package org.kitteh.craftirc.util.loadable;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.irc.client.library.util.Sanity;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages loadable types.
 */
public abstract class LoadableTypeManager<Type extends Loadable> {
    private class LoadableLoadout {
        private final Class<? extends Type> clazz;
        private final Constructor<? extends Type> constructor;
        private final List<LoadableField> fields;

        private LoadableLoadout(@Nonnull Class<? extends Type> clazz, @Nonnull Constructor<? extends Type> constructor, @Nonnull List<LoadableField> fields) {
            this.clazz = clazz;
            this.constructor = constructor;
            this.fields = fields;
        }

        @Nonnull
        private Class<? extends Type> getClazz() {
            return this.clazz;
        }

        @Nonnull
        private Constructor<? extends Type> getConstructor() {
            return this.constructor;
        }

        @Nonnull
        private List<LoadableField> getFields() {
            return this.fields;
        }
    }

    private class LoadableField {
        private final Field field;
        private final String name;
        private final boolean required;

        private LoadableField(@Nonnull String name, @Nonnull Field field, boolean required) {
            this.field = field;
            this.name = name;
            this.required = required;
        }

        @Nonnull
        private Field getField() {
            return this.field;
        }

        @Nonnull
        private String getName() {
            return this.name;
        }

        private boolean isRequired() {
            return this.required;
        }
    }

    private final Map<Class<?>, ArgumentProvider<?>> argumentProviders = new ConcurrentHashMap<>();
    private final Map<String, LoadableLoadout> types = new ConcurrentHashMap<>();
    private final CraftIRC plugin;
    private final Map<String, List<Map<Object, Object>>> unRegistered = new ConcurrentHashMap<>();
    private final Class<Type> clazz;

    protected LoadableTypeManager(@Nonnull CraftIRC plugin, @Nonnull Class<Type> clazz) {
        this.clazz = clazz;
        this.plugin = plugin;
    }

    protected void loadList(@Nonnull List<Object> list) {
        for (final Object listElement : list) {
            final Map<Object, Object> data;
            if ((data = MapGetter.castToMap(listElement)) == null) {
                continue;
            }
            final String type;
            if ((type = MapGetter.getString(data, "type")) == null) {
                this.processInvalid("No type set", data);
                continue;
            }
            final LoadableLoadout loadout = this.types.get(type);
            if (loadout == null) {
                List<Map<Object, Object>> unregged = this.unRegistered.get(type);
                if (unregged == null) {
                    unregged = new LinkedList<>();
                    this.unRegistered.put(type, unregged);
                }
                unregged.add(data);
                continue;
            }
            this.load(type, loadout, data);
        }
    }

    @Nonnull
    protected CraftIRC getCraftIRC() {
        return this.plugin;
    }

    private void load(@Nonnull String type, @Nonnull LoadableLoadout loadout, @Nonnull Map<Object, Object> data) {
        Class<?>[] parameterTypes = loadout.getConstructor().getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < args.length; i++) {
            if (parameterTypes[i].equals(CraftIRC.class)) {
                args[i] = this.plugin;
            } else if (this.argumentProviders.containsKey(parameterTypes[i])) {
                args[i] = this.argumentProviders.get(parameterTypes[i]).getArgument();
            }
        }
        Type loaded;
        try {
            loaded = loadout.getConstructor().newInstance(args);
            for (LoadableField field : loadout.getFields()) {
                Object o = MapGetter.get(data, field.getName(), field.getField().getType());
                if (field.isRequired() && o == null) {
                    throw new CraftIRCInvalidConfigException(String.format("Missing required field '%s' for type '%s'", field.getName(), type));
                }
                field.getField().set(loaded, o);
            }
            loaded.load(this.plugin, data);
            this.processCompleted(loaded);
        } catch (Exception e) {
            this.processFailedLoad(e, data);
        }

    }

    /**
     * Registers an ArgumentProvider for a particular class
     *
     * @param clazz class to register
     * @param provider provider to register
     * @return previously registered provider for given class, else null
     * @throws IllegalArgumentException for null class or provider
     */
    @Nonnull
    public final <Argument> ArgumentProvider<? extends Argument> registerArgumentProvider(@Nonnull Class<Argument> clazz, @Nonnull ArgumentProvider<? extends Argument> provider) {
        Sanity.nullCheck(clazz, "Cannot register a null class");
        Sanity.nullCheck(provider, "Cannot register a null provider");
        @SuppressWarnings("unchecked")
        ArgumentProvider<? extends Argument> old = (ArgumentProvider<? extends Argument>) this.argumentProviders.put(clazz, provider);
        return old;
    }

    /**
     * Registers a Loadable type by {@link Loadable.Type} name. Loadable
     * types registered here can be processed for loading from configuration.
     * <p/>
     * Names are unique and may not be registered twice.
     * <p/>
     * Classes must have a public constructor. The first constructor found is
     * the constructor used. The following types can be specified as
     * constructor parameters, with all others being passed null unless an
     * {@link org.kitteh.craftirc.util.loadable.ArgumentProvider} has been
     * registered for the type:
     * <ul>
     * <li>
     * {@link CraftIRC} - Is passed the CraftIRC instance
     * </li>
     * </ul>
     *
     * @param clazz class of the Loadable type to be registered
     * @throws IllegalArgumentException for null classes, classes not of the
     * manager's type, classes without the {@link Loadable.Type} annotation,
     * classes without public constructors, or for duplicate name submissions
     */
    public final void registerType(@Nonnull Class<? extends Type> clazz) {
        Sanity.nullCheck(clazz, "Cannot register a null class");
        Sanity.truthiness(this.clazz.isAssignableFrom(clazz), "Submitted class '" + clazz.getSimpleName() + "' is not of type " + this.clazz.getSimpleName());

        Constructor[] constructors = clazz.getConstructors();
        Sanity.truthiness(constructors.length > 0, "Class '" + clazz.getSimpleName() + "' lacks a public constructor");
        @SuppressWarnings("unchecked")
        Constructor<? extends Type> constructor = constructors[0];

        final Loadable.Type type = clazz.getAnnotation(Loadable.Type.class);
        Sanity.nullCheck(type, "Submitted class '" + clazz.getSimpleName() + "' has no Loadable.Type annotation");
        final String name = type.name();
        if (this.types.containsKey(name)) {
            throw new IllegalArgumentException(this.clazz.getSimpleName() + " type name '" + name + "' is already registered to '" + this.types.get(name).getClazz().getSimpleName() + "' and cannot be registered by '" + clazz.getSimpleName() + "'");
        }

        Map<String, LoadableField> fieldMap = new HashMap<>();
        this.mapFields(fieldMap, clazz);
        List<LoadableField> fields = new LinkedList<>(fieldMap.values());
        LoadableLoadout loadout = new LoadableLoadout(clazz, constructor, fields);

        this.types.put(name, loadout);
        if (this.unRegistered.containsKey(name)) {
            for (final Map<Object, Object> data : this.unRegistered.get(name)) {
                this.load(name, loadout, data);
            }
        }
    }

    private void mapFields(@Nonnull Map<String, LoadableField> map, @Nonnull Class<? extends Type> clazz) {
        if (this.clazz.isAssignableFrom(clazz.getSuperclass())) {
            @SuppressWarnings("unchecked")
            Class<? extends Type> superClass = (Class<? extends Type>) clazz.getSuperclass();
            mapFields(map, superClass);
        }
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            Load loadData;
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || (loadData = field.getAnnotation(Load.class)) == null) {
                continue;
            }
            field.setAccessible(true);
            String confName = loadData.name().isEmpty() ? field.getName() : loadData.name();
            map.put(confName, new LoadableField(confName, field, loadData.required()));
        }
    }

    protected abstract void processCompleted(@Nonnull Type loaded) throws CraftIRCInvalidConfigException;

    protected abstract void processFailedLoad(@Nonnull Exception exception, @Nonnull Map<Object, Object> data);

    protected abstract void processInvalid(@Nonnull String reason, @Nonnull Map<Object, Object> data);
}