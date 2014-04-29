package org.kitteh.craftirc.endpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Designates an {@link Endpoint} which must run on the main server thread.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SyncEndpoint {
}