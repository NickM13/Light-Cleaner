package com.bergerkiller.bukkit.lightcleaner;

import com.bergerkiller.bukkit.common.permissions.PermissionEnum;
import org.bukkit.permissions.PermissionDefault;

public class Permission extends PermissionEnum {
    public static final Permission CLEAN = new Permission("lightcleaner.clean", PermissionDefault.OP, "Allows a player to fix lighting issues. All commands require this permission.");
    public static final Permission CLEAN_AREA = new Permission("lightcleaner.clean.area", PermissionDefault.OP, "Allows a player to fix lighting issues in chunks around him with any radius");
    public static final Permission CLEAN_BY_RADIUS = new Permission("lightcleaner.clean.radius", PermissionDefault.OP, "Allows a player to only fix a specific radius of chunks (example perm: lightcleaner.clean.radius.4)", 1);
    public static final Permission CLEAN_WORLD = new Permission("lightcleaner.clean.world", PermissionDefault.OP, "Allows a player to fix lighting issues in all the chunks of an entire world");
    public static final Permission STATUS = new Permission("lightcleaner.status", PermissionDefault.OP, "Allows a player to check the status of ongoing lighting operations");
    public static final Permission ABORT = new Permission("lightcleaner.abort", PermissionDefault.OP, "Allows a player to abort all current lighting operations");
    public static final Permission PAUSE = new Permission("lightcleaner.pause", PermissionDefault.OP, "Allows a player to pause and resume lighting operations");
    public static final Permission DIRTY_DEBUG = new Permission("lightcleaner.dirty.debug", PermissionDefault.FALSE, "Allows a player to corrupt lighting instead of clean it (for debugging purposes)");

    private Permission(final String path, final PermissionDefault def, final String desc) {
        super(path, def, desc);
    }

    private Permission(final String path, final PermissionDefault def, final String desc, int argCount) {
        super(path, def, desc, argCount);
    }
}