package com.github.cozyplugins.cozywarps;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a visit to a warp.
 */
public class WarpVisit {

    private final @NotNull UUID playerUuid;
    private final @NotNull UUID warpUuid;

    /**
     * Used to create a warp visit instance.
     *
     * @param warpUuid The warp that was visited.
     * @param playerUuid The uuid of the player visiting the warp.
     */
    public WarpVisit(@NotNull UUID warpUuid, @NotNull UUID playerUuid) {
        this.warpUuid = warpUuid;
        this.playerUuid = playerUuid;
    }

    public boolean isWarpUuid(@NotNull UUID warpUuid) {
        return this.warpUuid == warpUuid;
    }

    public boolean isPlayerUuid(@NotNull UUID playerUuid) {
        return this.playerUuid == playerUuid;
    }
}
