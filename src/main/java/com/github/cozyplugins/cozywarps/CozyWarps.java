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

import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ConfirmAction;
import com.github.cozyplugins.cozylibrary.inventory.inventory.ConfirmationInventory;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozywarps.command.WarpsCommand;
import com.github.smuddgge.squishyconfiguration.ConfigurationFactory;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the main class.
 */
public final class CozyWarps extends CozyPlugin {

    private static @NotNull CozyWarps instance;

    private @NotNull Configuration warpConfig;
    private @NotNull Configuration banConfig;
    private @NotNull Configuration config;
    private @NotNull List<WarpVisit> warpVisitList = new ArrayList<>();

    @Override
    public boolean enableCommandDirectory() {
        return true;
    }

    @Override
    public void onCozyEnable() {

        // Initialise the warp visit list.
        this.warpVisitList = new ArrayList<>();

        // Create the instance of the storage configuration.
        this.warpConfig = ConfigurationFactory.YAML.create(this.getDataFolder(), "storage");
        this.warpConfig.load();

        this.banConfig = ConfigurationFactory.YAML.create(this.getDataFolder(), "bans");
        this.banConfig.load();

        // Create the instance of the config file.
        this.config = ConfigurationFactory.YAML.create(this.getDataFolder(), "config");
        this.config.setDefaultPath("config.yml");
        this.config.load();

        // Initialise the instance getter.
        CozyWarps.instance = this;

        // Add all the command types.
        this.addCommandType(new WarpsCommand());
    }

    /**
     * Used to get the configuration file.
     *
     * @return The configuration file.
     */
    public @NotNull Configuration getCozyWarpsConfig() {
        return this.config;
    }

    /**
     * Used to get the price a warp would cost.
     *
     * @return The price of the warp.
     */
    public int getPrice(Player player) {

        if (player.hasPermission("cozywarps.createfee.bypass")){
            return 0;
        }
        else{
            return this.config.getInteger("warpcost");
        }
    }

    /**
     * Used to get the max amount of warps per player
     *
     * @return The max amount of warps per player
     */
    public int getMaxWarps(){
        return  this.config.getInteger("maxwarps");
    }

    /**
     * Used to get a warp from storage given its
     * unique identifier.
     *
     * @param identifier The warps unique identifier.
     * @return The optional warp.
     */
    public @NotNull Optional<Warp> getWarp(@NotNull UUID identifier) {
        if (this.warpConfig.getKeys().contains(identifier.toString())) {
            return Optional.of(new Warp(identifier).convert(this.warpConfig.getSection(identifier.toString())));
        }

        return Optional.empty();
    }

    /**
     * Used to get a warp a player owns.
     * A player cannot have two warps named the same.
     *
     * @param playerUuid The player's uuid.
     * @param warpName   The warp's name.
     * @return The optional warp.
     */
    public @NotNull Optional<Warp> getWarp(@NotNull UUID playerUuid, @NotNull String warpName) {
        for (Warp warp : this.getAllWarps()) {
            if (!warp.getManagerUuid().equals(playerUuid)) continue;
            if (!warp.getName().equals(warpName)) continue;
            return Optional.of(warp);
        }
        return Optional.empty();
    }

    /**
     * Used to get the list of all warps.
     *
     * @return The list of all warps.
     */
    public @NotNull List<Warp> getAllWarps() {
        List<Warp> list = new ArrayList<>();

        // Loop though all keys.
        for (String key : this.warpConfig.getKeys()) {
            list.add(new Warp(UUID.fromString(key)).convert(this.warpConfig.getSection(key)));
        }

        return list;
    }

    /**
     * Used to get all warps a player owns.
     *
     * @param playerUuid The player uuid.
     * @return The list of their warps.
     */
    public @NotNull List<Warp> getAllWarps(@NotNull UUID playerUuid) {
        List<Warp> list = new ArrayList<>();
        for (Warp warp : this.getAllWarps()) {
            if (warp.getManagerUuid().equals(playerUuid)) list.add(warp);
        }
        return list;
    }

    /**
     * Used to get the number of warps owned by a specific player.
     *
     * @param uuid The players uuid.
     * @return The number of warps they own.
     */
    public int getAmountOwned(@NotNull UUID uuid) {
        int amountOwned = 0;
        for (Warp warp : this.getAllWarps()) {
            if (warp.getManagerUuid().equals(uuid)) amountOwned++;
        }
        return amountOwned;
    }

    /**
     * Used to get the warp owners names.
     *
     * @return The names of the warp owners.
     */
    public @NotNull List<String> getManagerNames() {
        List<String> list = new ArrayList<>();
        for (Warp warp : this.getAllWarps()) {
            if (list.contains(warp.getManagerName())) continue;
            list.add(warp.getManagerName());
        }
        return list;
    }

    /**
     * Used to get the list of banned player names
     * from an owners warps.
     *
     * @param playerUuid The owners uuid.
     * @return The list of banned names.
     */
    public @NotNull List<String> getBannedPlayers(@NotNull UUID playerUuid) {
        List<String> playerNameList = new ArrayList<>();
        for (String uuidString : this.banConfig.getListString(playerUuid.toString(), new ArrayList<>())) {
            UUID uuid = UUID.fromString(uuidString);
            playerNameList.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return playerNameList;
    }

    /**
     * Used to add a warp visit to the visit list.
     *
     * @param warpVisit The instance of the warp visit.
     * @return This instance.
     */
    public @NotNull CozyWarps addWarpVisit(@NotNull WarpVisit warpVisit) {
        this.warpVisitList.add(warpVisit);
        return this;
    }

    /**
     * Used to remove all the warp visits registered.
     *
     * @return This instance.
     */
    public @NotNull CozyWarps removeWarpVisits() {
        this.warpVisitList = new ArrayList<>();
        return this;
    }

    /**
     * Used to check if the list of visits contains a certain
     * warp uuid and player uuid.
     *
     * @param warpUuid   The warp uuid to check.
     * @param playerUuid The player uuid to check.
     * @return True if they have visited recently.
     */
    public boolean hasVisited(@NotNull UUID warpUuid, @NotNull UUID playerUuid) {
        for (WarpVisit warpVisit : this.warpVisitList) {
            if (warpVisit.isWarpUuid(warpUuid) && warpVisit.isPlayerUuid(playerUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used to check if a player is banned from another player's warps.
     *
     * @param playerUuid The players uuid.
     * @param ownerUuid  The owners uuid.
     * @return True if they are banned.
     */
    public boolean isBanned(@NotNull UUID playerUuid, @NotNull UUID ownerUuid) {
        List<String> bannedPlayers = this.banConfig.getListString(ownerUuid.toString());
        return bannedPlayers.contains(playerUuid.toString());
    }

    /**
     * Used to update or insert a warp in storage.
     *
     * @param warp The instance of the warp to update or insert.
     * @return This instance.
     */
    public @NotNull CozyWarps updateWarp(@NotNull Warp warp) {
        this.warpConfig.set(warp.getIdentifier().toString(), warp.convert().getMap());
        this.warpConfig.save();
        return this;
    }

    /**
     * Used to remove a warp from storage.
     *
     * @param playerName The player's name.
     * @param warpName   The name of the warp.
     * @return This instance.
     */
    public @NotNull CozyWarps removeWarp(@NotNull String playerName, @NotNull String warpName) {
        for (Warp warp : this.getAllWarps()) {

            // Check the credentials.
            if (!Bukkit.getOfflinePlayer(playerName).getUniqueId().equals(warp.getManagerUuid())) continue;
            if (!warpName.equals(warp.getName())) continue;

            this.warpConfig.set(warp.getIdentifier().toString(), null);
            this.warpConfig.save();
            return this;
        }
        return this;
    }

    /**
     * Used to ban a player from an owners warps.
     *
     * @param playerUuid The player's uuid to ban.
     * @param ownerUuid  The owner's uuid.
     * @return This instance.
     */
    public @NotNull CozyWarps banPlayer(@NotNull UUID playerUuid, @NotNull UUID ownerUuid) {
        List<String> list = this.banConfig.getListString(ownerUuid.toString(), new ArrayList<>());
        list.add(playerUuid.toString());
        this.banConfig.set(ownerUuid.toString(), list);
        this.banConfig.save();
        return this;
    }

    /**
     * Used to unban a player from an owners warps.
     *
     * @param playerUuid The player to unban.
     * @param ownerUuid  The owner of the warps.
     * @return This instance.
     */
    public @NotNull CozyWarps unBanPlayer(@NotNull UUID playerUuid, @NotNull UUID ownerUuid) {
        List<String> list = this.banConfig.getListString(ownerUuid.toString(), new ArrayList<>());
        list.remove(playerUuid.toString());
        this.banConfig.set(ownerUuid.toString(), list);
        this.banConfig.save();
        return this;
    }

    /**
     * Used to start the bukkit task of removing
     * visits every hour.
     */
    public void startVisitRemovingTask() {

        // Create the task.
        Bukkit.getScheduler().runTaskTimer(
                this,
                this::removeWarpVisits,
                72000,
                72000
        );
    }

    /**
     * Used to create a warp.
     * It will check before creating.
     *
     * @param user The instance of the user creating the warp.
     * @param name The name of the warp.
     * @param location The location of the warp.
     */
    public void createWarp(@NotNull PlayerUser user, @NotNull String name, @NotNull Location location) {

        Warp warp = new Warp(UUID.randomUUID());
        warp.setLocation(location);

        // Check the warp's name.
        if (CozyWarps.getInstance().getWarp(user.getUuid(), name).isPresent()) {
            user.sendMessage("&7&l> &7You already have a warp with this name. Please use a different name.");
            return;
        }

        // Check if the location is safe.
        if (!warp.isSafe()) {
            user.sendMessage("&7&l> &7This location is not safe for players to warp to.");
            user.sendMessage("&7- &fThe block below you should not be air.");
            return;
        }

        // Get the warp cost
        final int cost = CozyWarps.getInstance().getPrice(user.getPlayer());

        // Check if the player can no longer buy any more warps.
        if (getAmountOwned(user.getUuid()) >= getMaxWarps()) {
            user.sendMessage("&7&l> &7You already own the maximum amount of warps.");
            return;
        }

        // Check if the player has enough money.
        if (user.getMoney() < cost) {
            user.sendMessage("&7&l> &7You do not have enough money to buy another warp. Another warp costs &f" + cost + " coins");
            return;
        }

        // Create a configuration menu.
        ConfirmationInventory inventory = new ConfirmationInventory(
                new ConfirmAction()
                        .setAnvilTitle("&7&lBuy for &8&l" + cost + " coins")
                        .setAbort(playerUser -> {
                            playerUser.sendMessage("&7Aborted warp creation.");
                        })
                        .setConfirm(playerUser -> {
                            playerUser.removeMoney(cost);
                            playerUser.sendMessage("&7&l> &7You have brought another warp for &f" + cost + " coins");

                            // Create the warp credentials.
                            warp.setCreatorUuid(user.getUuid());
                            warp.setManagerUuid(user.getUuid());
                            warp.setName(name);
                            warp.save();

                            // Send the player a message.
                            user.sendMessage("&7&l> &7Created a new warp with name &f" + name + "&7.");
                        })
        );
        inventory.open(user.getPlayer());
    }

    /**
     * Used to get the instance of the plugin.
     *
     * @return The instance of the plugin.
     */
    public static @NotNull CozyWarps getInstance() {
        return CozyWarps.instance;
    }
}
