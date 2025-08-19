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

package com.github.cozyplugins.cozywarps.command;

import com.github.cozyplugins.cozylibrary.command.command.CommandType;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandArguments;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandStatus;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandSuggestions;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandTypePool;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ConfirmAction;
import com.github.cozyplugins.cozylibrary.inventory.inventory.ConfirmationInventory;
import com.github.cozyplugins.cozylibrary.user.ConsoleUser;
import com.github.cozyplugins.cozylibrary.user.FakeUser;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozylibrary.user.User;
import com.github.cozyplugins.cozywarps.CozyWarps;
import com.github.cozyplugins.cozywarps.Warp;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the warp delete command.
 * Used to delete a warp.
 */
public class WarpsDeleteCommand implements CommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "delete";
    }

    @Override
    public @Nullable String getSyntax() {
        return "/[parent] [name] <name>";
    }

    @Override
    public @Nullable String getDescription() {
        return "Used to delete a warp.";
    }

    @Override
    public @Nullable CommandTypePool getSubCommandTypes() {
        return null;
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {

        // Check if they have staff permissions.
        if (user.hasPermission("cozywarps.staff")) {

            // Create the base suggestions.
            CommandSuggestions suggestions = new CommandSuggestions()
                    .append(CozyWarps.getInstance().getManagerNames());

            // Add the players warps.
            if (!arguments.getArguments().isEmpty() && !arguments.getArguments().get(0).isEmpty()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(arguments.getArguments().get(0));
                suggestions.append(CozyWarps.getInstance()
                        .getAllWarps(player.getUniqueId()).stream().map(Warp::getName).toList()
                );
            }
            return suggestions;
        }

        return new CommandSuggestions().append(CozyWarps.getInstance()
                .getAllWarps(user.getUuid()).stream().map(Warp::getName).toList()
        );
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onPlayer(@NotNull PlayerUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {

        // Check if there are no arguments.
        if (arguments.getArguments().isEmpty()) {
            user.sendMessage("&7&l> &7Please provide the correct arguments.");
            return new CommandStatus();
        }

        // Check if they player has the staff permission.
        if (user.hasPermission("cozywarps.staff")) {

            // Check they have given the correct number of arguments.
            if (arguments.getArguments().size() < 2) {
                user.sendMessage("&7&l> &7Please provide a player name and there warp.");
                return new CommandStatus();
            }

            final String playerName = arguments.getArguments().get(0);
            final String warpName = arguments.getArguments().get(1);

            ConfirmationInventory inventory = new ConfirmationInventory(
                    new ConfirmAction()
                            .setAnvilTitle("&8&lDelete " + warpName)
                            .setAbort(playerUser -> {
                                user.sendMessage("&7&l> &7Aborted deletion of " + warpName + ".");
                            })
                            .setConfirm(playerUser -> {
                                user.sendMessage("&7&l> &7Deleting warp " + warpName + ".");
                                CozyWarps.getInstance().removeWarp(playerName, warpName);
                            })
            );
            inventory.open(user.getPlayer());
            return new CommandStatus();
        }

        // Get the warp name.
        final String warpName = arguments.getArguments().get(0);

        ConfirmationInventory inventory = new ConfirmationInventory(
                new ConfirmAction()
                        .setAnvilTitle("&8&lDelete " + warpName)
                        .setAbort(playerUser -> {
                            user.sendMessage("&7&l> &7Aborted deletion of " + warpName + ".");
                        })
                        .setConfirm(playerUser -> {
                            user.sendMessage("&7&l> &7Deleting warp " + warpName + ".");
                            CozyWarps.getInstance().removeWarp(user.getName(), warpName);
                        })
        );
        inventory.open(user.getPlayer());

        return new CommandStatus();
    }

    @Override
    public @Nullable CommandStatus onFakeUser(@NotNull FakeUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onConsole(@NotNull ConsoleUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }
}
