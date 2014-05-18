package org.kitteh.craftirc.exceptions;

/**
 * So sad.
 */
public class CraftIRCWillLeakTearsException extends RuntimeException {
    public CraftIRCWillLeakTearsException() {
        super("CraftIRC's logger was called while CraftIRC isn't enabled");
    }
}