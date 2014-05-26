package org.kitteh.craftirc.util;

import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Stick them with it.
 */
public abstract class PointyEnd extends Endpoint {
    private static final Constructor<EndpointFilterLoader> LOADER_CONSTRUCTOR;
    private static final Method RECEIVE_MESSAGE;

    static {
        try {
            LOADER_CONSTRUCTOR = EndpointFilterLoader.class.getDeclaredConstructor(Endpoint.class);
            LOADER_CONSTRUCTOR.setAccessible(true);
            RECEIVE_MESSAGE = Endpoint.class.getDeclaredMethod("receiveMessage", Message.class);
            RECEIVE_MESSAGE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    private EndpointFilterLoader loader;

    public final EndpointFilterLoader getLoader() {
        if (this.loader != null) {
            return loader;
        }
        try {
            return this.loader = LOADER_CONSTRUCTOR.newInstance(this);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public void message(Message message) {
        try {
            RECEIVE_MESSAGE.invoke(this, message);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}