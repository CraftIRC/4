package org.kitteh.craftirc.exceptions;

import java.io.IOException;

/**
 * Invalid configs trigger these
 */
public class CraftIRCInvalidConfigException extends Exception {
    public CraftIRCInvalidConfigException(String message) {
        super(message);
    }

    public CraftIRCInvalidConfigException(IOException e) {
        super("Could not load config file. See related exception", e);
    }
}