package org.kitteh.craftirc.endpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Labels a class as a named Endpoint for registration in
 * {@link EndpointManager#registerEndpointType(Class)}.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EndpointType {
    /**
     * Names the labeled Endpoint.
     *
     * @return unique name for this Endpoint type
     */
    String name();

    /**
     * Determines if the Endpoint will receive messages on the server thread.
     *
     * @return true if messages will be sent on the main thread
     */
    boolean sync() default false;
}