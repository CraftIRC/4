package org.kitteh.craftirc.util;

import org.apache.commons.lang.Validate;

import java.util.UUID;

/**
 * Represents an ingame player
 */
public final class MinecraftPlayer {
    private final String name;
    private final UUID uniqueId;

    /**
     * Creates a MinecraftPlayer instance.
     *
     * @param name current name of the player
     * @param uniqueId the player's UUID
     */
    public MinecraftPlayer(String name, UUID uniqueId) {
        Validate.notNull(name, "Name cannot be null");
        Validate.notNull(uniqueId, "uniqueId cannot be null");
        this.name = name;
        this.uniqueId = uniqueId;
    }

    /**
     * Gets the name of this player.
     *
     * @return the player's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the UUID of this player.
     *
     * @return the player's UUID
     */
    public UUID getUniqueID() {
        return this.uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MinecraftPlayer) {
            MinecraftPlayer p = (MinecraftPlayer) o;
            return this.name.equals(p.name) && this.uniqueId.equals(p.uniqueId);
        }
        return false;
    }
}