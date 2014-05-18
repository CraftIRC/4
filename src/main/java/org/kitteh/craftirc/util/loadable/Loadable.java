package org.kitteh.craftirc.util.loadable;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * Represents an object that can be loaded from config.
 */
public abstract class Loadable {
    @Retention(value = RetentionPolicy.RUNTIME)
    public @interface Type {
        String name();
    }

    /**
     * Loads data from config, if any is present.
     *
     * @param data data to load
     * @throws CraftIRCInvalidConfigException if invalid
     */
    protected abstract void load(CraftIRC plugin, Map<Object, Object> data) throws CraftIRCInvalidConfigException;
}