/*
 * CozyWarps - Used to create player warps.
 * Copyright (C) 2024 CozyPlugins
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.cozyplugins.cozywarps.indicator;

import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

/**
 * Indicates if a class can convert locations
 * into configuration sections.
 */
public interface LocationConverter {

    /**
     * Used to get a location has a configuration section.
     *
     * @param location The instance of a location.
     * @return The location has a configuration section.
     */
    default @NotNull ConfigurationSection getLocationAsConfigurationSection(@NotNull Location location) {

        // Create the section.
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        // Add location infomation.
        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
        section.set("pitch", Float.toString(location.getPitch()));
        section.set("yaw", Float.toString(location.getYaw()));

        if (location.getWorld() == null) return section;
        section.set("world", location.getWorld().getName());

        return section;
    }

    /**
     * Used to get a configuration section has a location.
     *
     * @param section The instance of the configuration section.
     * @return The instance of the location.
     */
    default @Nullable Location getConfigurationSectionAsLocation(@NotNull ConfigurationSection section) {

        // Check if the world exist.
        World world = Bukkit.getWorld(section.getString("world", "null"));
        if (world == null) return null;

        // Return the location.
        return new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                Float.parseFloat(section.getString("pitch")),
                Float.parseFloat(section.getString("yaw"))
        );
    }
}
