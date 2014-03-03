package org.kitteh.craftirc.exceptions;

/**
 * OH NO, TABS
 */
public final class CraftIRCFoundTabsException extends CraftIRCInvalidConfigException {
    public CraftIRCFoundTabsException(int lineNumber, String line) {
        super("Config cannot contain tabs. Found a tab on line " + lineNumber + ": " + line.replace("\t", ""));
    }
}