/*
 *     CozyWarps - Used to create player warps.
 *     Copyright (C) 2024 CozyPlugins
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
     * @param warpUuid   The warp that was visited.
     * @param playerUuid The uuid of the player visiting the warp.
     */
    public WarpVisit(@NotNull UUID warpUuid, @NotNull UUID playerUuid) {
        this.warpUuid = warpUuid;
        this.playerUuid = playerUuid;
    }

    public boolean isWarpUuid(@NotNull UUID warpUuid) {
        return this.warpUuid.equals(warpUuid);
    }

    public boolean isPlayerUuid(@NotNull UUID playerUuid) {
        return this.playerUuid.equals(playerUuid);
    }
}
