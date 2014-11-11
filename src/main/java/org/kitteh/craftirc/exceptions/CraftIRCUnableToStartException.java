package org.kitteh.craftirc.exceptions;

/**
 * Thrown if CraftIRC fails to start.
 */
public class CraftIRCUnableToStartException extends Exception {
    public CraftIRCUnableToStartException(String message, Exception reason) {
        super(message, reason);
    }
}