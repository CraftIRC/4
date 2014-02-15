package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.filter.Filter;

public final class BukkitPermissionFilter implements Filter {
    private String permission;

    public BukkitPermissionFilter(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return this.permission;
    }
}