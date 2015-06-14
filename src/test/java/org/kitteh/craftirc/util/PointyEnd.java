package org.kitteh.craftirc.util;

import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.link.Link;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Stick them with it.
 */
public class PointyEnd extends Link {
    private static final Constructor<LinkFilterLoader> LOADER_CONSTRUCTOR;
    private static final Method RECEIVE_MESSAGE;

    static {
        try {
            LOADER_CONSTRUCTOR = LinkFilterLoader.class.getDeclaredConstructor(Link.class);
            LOADER_CONSTRUCTOR.setAccessible(true);
            RECEIVE_MESSAGE = Link.class.getDeclaredMethod("filterMessage", TargetedMessage.class);
            RECEIVE_MESSAGE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    private LinkFilterLoader loader;

    public PointyEnd() {
        super(null, "", "", null);
    }

    public final LinkFilterLoader getLoader() {
        if (this.loader != null) {
            return loader;
        }
        try {
            return this.loader = LOADER_CONSTRUCTOR.newInstance(this);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public void message(TargetedMessage message) {
        try {
            RECEIVE_MESSAGE.invoke(this, message);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}