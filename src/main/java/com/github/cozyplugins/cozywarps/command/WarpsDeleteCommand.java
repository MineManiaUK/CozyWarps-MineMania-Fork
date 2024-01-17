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

import java.util.UUID;

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
        if (user.hasPermission("cozywarps.staff")) {

            // Create the base suggestions.
            CommandSuggestions suggestions = new CommandSuggestions()
                    .append(CozyWarps.getInstance().getOwnerNames());

            // Add the players warps.
            if (!arguments.getArguments().isEmpty()) {
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
            user.sendMessage("&7Please provide the correct arguments.");
            return new CommandStatus();
        }

        // Check if they player has the staff permission.
        if (user.hasPermission("cozywarps.staff")) {

            // Check they have given the correct number of arguments.
            if (arguments.getArguments().size() < 2) {
                user.sendMessage("&7Please provide a player name and there warp.");
                return new CommandStatus();
            }

            final String playerName = arguments.getArguments().get(0);
            final String warpName = arguments.getArguments().get(1);

            ConfirmationInventory inventory = new ConfirmationInventory(
                    new ConfirmAction()
                            .setAnvilTitle("&8&lDelete " + warpName)
                            .setAbort(playerUser -> {
                                user.sendMessage("&7Aborted deletion of " + warpName + ".");
                            })
                            .setConfirm(playerUser -> {
                                user.sendMessage("&7Deleting warp " + warpName + ".");
                                CozyWarps.getInstance().removeWarp(playerName, warpName);
                            })
            );
            inventory.open(user.getPlayer());
        }
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
