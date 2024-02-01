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

package com.github.cozyplugins.cozywarps;

import com.github.cozyplugins.cozylibrary.indicator.ConfigurationConvertable;
import com.github.cozyplugins.cozylibrary.indicator.Replicable;
import com.github.cozyplugins.cozylibrary.indicator.Savable;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozywarps.indicator.LocationConverter;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Represents a warp player's can warp to.
 */
public class Warp implements ConfigurationConvertable<Warp>, Replicable<Warp>, Savable, Comparable<Warp>, LocationConverter {

    private final @NotNull UUID identifier;
    private @NotNull UUID ownerUuid;
    private @NotNull String name;
    private @Nullable String description;
    private @NotNull Material material;

    private @Nullable Location location;
    private int visits;

    /**
     * Used to create a warp instance.
     *
     * @param identifier The warps unique identifier.
     */
    public Warp(@NotNull UUID identifier) {
        this.identifier = identifier;
        this.ownerUuid = UUID.randomUUID();
        this.name = "null";
        this.material = Material.COMPASS;
    }

    /**
     * Used to get the warps unique identifier.
     *
     * @return The warps unique identifier.
     */
    public @NotNull UUID getIdentifier() {
        return this.identifier;
    }

    /**
     * Used to get the owner's uuid.
     *
     * @return The owner's uuid.
     */
    public @NotNull UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    /**
     * Used to get the owner's name.
     *
     * @return The owner's name.
     */
    public @NotNull String getOwnerName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.ownerUuid);
        if (player.getName() == null) return "Null";
        return player.getName();
    }

    /**
     * Used to get the warp's name.
     *
     * @return The warp's name.
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Used to get the warp's description.
     *
     * @return The warp's description.
     */
    public @Nullable String getDescription() {
        return this.description;
    }

    /**
     * Used to get the material used for the
     * warp's icon.
     *
     * @return The warp's material.
     */
    public @NotNull Material getMaterial() {
        return this.material;
    }

    /**
     * Used to get the warp's location.
     *
     * @return The warp's location.
     */
    public @Nullable Location getLocation() {
        return this.location;
    }

    public @NotNull ConfigurationSection getLocationAsSection() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());
        if (this.location == null) return section;
        return this.getLocationAsConfigurationSection(this.location);
    }

    /**
     * Used to get the number of visits this warp has had.
     *
     * @return The number of visits.
     */
    public int getVisits() {
        return this.visits;
    }

    /**
     * Used to set the owner's uuid.
     *
     * @param ownerUuid The owner's uuid.
     * @return This instance.
     */
    public @NotNull Warp setOwnerUuid(@NotNull UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
        return this;
    }

    /**
     * Used to set the name of the warp.
     * The name cannot be null.
     *
     * @param name The name of the warp to set.
     * @return This instance.
     */
    public @NotNull Warp setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    /**
     * Used to set the description of the warp.
     *
     * @param description The description of the warp.
     * @return This instance.
     */
    public @NotNull Warp setDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Used to set the material that should be
     * used as the warp's icon.
     *
     * @param material The material of the icon.
     * @return This instance.
     */
    public @NotNull Warp setMaterial(@NotNull Material material) {
        this.material = material;
        return this;
    }

    /**
     * Used to set the material that should be
     * used as the warp's icon.
     * This value is given as a string that will be
     * converted into the material enum.
     *
     * @param materialName The material's name to set as the icon.
     * @return This instance.
     */
    public @NotNull Warp setMaterialAsString(@NotNull String materialName) {
        this.material = Material.valueOf(materialName.toUpperCase());
        return this;
    }

    /**
     * Used to set the location of the warp.
     * This is where the players will be teleported to.
     *
     * @param location The location of the warp.
     * @return This instance.
     */
    public @NotNull Warp setLocation(@Nullable Location location) {
        this.location = location;
        return this;
    }

    /**
     * Used to set the location of the warp given a configuration
     * section with the location infomation.
     *
     * @param section The instance of the location configuration section.
     * @return This instance.
     */
    public @NotNull Warp setLocationAsConfigurationSection(@NotNull ConfigurationSection section) {
        this.location = this.getConfigurationSectionAsLocation(section);
        return this;
    }

    /**
     * Used to set the number of visits this warp should display.
     *
     * @param visits The number of visits to set this warp to.
     * @return This instance.
     */
    public @NotNull Warp setVisits(int visits) {
        this.visits = visits;
        return this;
    }

    /**
     * Used to increment the number of visits
     * to this warp.
     *
     * @return This instance.
     */
    public @NotNull Warp incrementVisits() {
        this.visits++;
        return this;
    }

    /**
     * Used to check if the warp's location
     * is a safe place to teleport.
     *
     * @return True if the location is safe.
     */
    public boolean isSafe() {

        // Check if the location doesn't exist.
        if (this.location == null) return false;

        // Check if the block below is air.
        return this.location.clone()
                .add(new Vector(0, -1, 0)).getBlock().getType() != Material.AIR;
    }

    /**
     * Used to get the warp as an inventory item.
     *
     * @return The instance of this as an inventory item.
     */
    public @NotNull InventoryItem createInventoryItem() {
        if (this.description != null) {
            return new InventoryItem()
                    .setMaterial(this.material)
                    .setName("&e&l" + this.name)
                    .setLore("&8&l&m------------",
                            "&f" + this.description,
                            "&7",
                            "&fOwner &a" + this.getOwnerName(),
                            "&fVisits &a" + this.visits);
        }

        return new InventoryItem()
                .setMaterial(this.material)
                .setName("&e&l" + this.name)
                .setLore("&8&l&m------------",
                        "&fOwner &a" + this.getOwnerName(),
                        "&fVisits &a" + this.visits);
    }

    /**
     * Used to teleport to the warp location.
     *
     * @param player The instance of the player to teleport.
     * @return This instance.
     */
    public @NotNull Warp teleport(PlayerUser player) {

        // Check if the location is safe.
        if (!this.isSafe()) {
            player.sendMessage("&7&l> &7Teleportation failed, warp location is unsafe.");
            return this;
        }

        assert this.location != null;
        player.getPlayer().teleport(this.location);
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection(new LinkedHashMap<>());

        section.set("owner", this.ownerUuid.toString());
        section.set("name", this.name);
        section.set("description", this.description);
        section.set("material", this.material.toString());
        section.set("location", this.getLocationAsSection().getMap());
        section.set("visits", this.visits);

        return section;
    }

    @Override
    public @NotNull Warp convert(ConfigurationSection section) {

        this.ownerUuid = UUID.fromString(section.getString("owner"));
        this.name = section.getString("name", "null");
        this.description = section.getString("description");
        this.setMaterialAsString(section.getString("material", "COMPASS"));
        this.setLocationAsConfigurationSection(section.getSection("location"));
        this.visits = section.getInteger("visits");

        return this;
    }

    @Override
    public Warp duplicate() {
        return new Warp(this.identifier).convert(this.convert());
    }

    @Override
    public void save() {
        CozyWarps.getInstance().updateWarp(this);
    }

    @Override
    public int compareTo(@NotNull Warp warp) {
        return Integer.compare(warp.getVisits(), this.visits);
    }
}
