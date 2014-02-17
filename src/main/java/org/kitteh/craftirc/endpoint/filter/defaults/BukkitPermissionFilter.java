package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.filter.Filter;

/**
 * A filter by permission node.
 */
public final class BukkitPermissionFilter implements Filter {
    private String permission;

    /**
     * Creates a filter by permission node.
     *
     * @param permission permission node to by which to filter
     */
    public BukkitPermissionFilter(String permission) {
        this.permission = permission;
    }

    /**
     * Gets the permission node being monitored.
     *
     * @return the permission node monitored
     */
    public String getPermission() {
        return this.permission;
    }
}