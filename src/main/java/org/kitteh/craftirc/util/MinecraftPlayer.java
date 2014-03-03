package org.kitteh.craftirc.util;

import java.util.UUID;

/**
 * Represents an ingame player
 */
public final class MinecraftPlayer {
    private UUID uniqueId;
    private String name;

    public MinecraftPlayer(String name, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUniqueID() {
        return this.uniqueId;
    }
}