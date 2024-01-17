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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Represents the warp create command.
 * Used to create a warp.
 */
public class WarpsCreateCommand implements CommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "create";
    }

    @Override
    public @Nullable String getSyntax() {
        return "/[parent] [name] <name>";
    }

    @Override
    public @Nullable String getDescription() {
        return "Used to create a warp.";
    }

    @Override
    public @Nullable CommandTypePool getSubCommandTypes() {
        return null;
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return new CommandSuggestions().append(List.of("<name>"));
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onPlayer(@NotNull PlayerUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {

        // Get the warp's name.
        final String name = arguments.getArguments().get(0);

        Warp warp = new Warp(UUID.randomUUID());
        warp.setLocation(user.getPlayer().getLocation());

        // Check the warp's name.
        if (CozyWarps.getInstance().getWarp(user.getUuid(), name).isPresent()) {
            user.sendMessage("&7You already have a warp with this name. Please use a different name.");
            return new CommandStatus();
        }

        // Check if the location is safe.
        if (!warp.isSafe()) {
            user.sendMessage("&7This location is not safe for players to warp to.");
            user.sendMessage("&7- &fThe block below you should not be air.");
            return new CommandStatus();
        }

        // Get the number of warps they own.
        final int amountOfWarpsOwned = CozyWarps.getInstance().getAmountOwned(user.getUuid());
        final int cost = CozyWarps.getInstance().getPrice(amountOfWarpsOwned + 1);

        // Check if the player can no longer buy any more warps.
        if (cost == -1) {
            user.sendMessage("&7You already own the maximum amount of warps.");
            return new CommandStatus();
        }

        // Check if the player has enough money.
        if (user.getMoney() < cost) {
            user.sendMessage("&7You do not have enough money to buy another warp. Another warp costs &f" + cost + " coins");
            return new CommandStatus();
        }

        // Create a configuration menu.
        ConfirmationInventory inventory = new ConfirmationInventory(
                new ConfirmAction()
                        .setAnvilTitle("&7&lBuy for &8&l" + cost + " coins")
                        .setAbort(playerUser -> {
                            playerUser.sendMessage("&7Aborted warp creation.");
                        })
                        .setConfirm(playerUser -> {
                            playerUser.sendMessage("&7You have brought another warp for &f" + cost + " coins");

                            // Create the warp credentials.
                            warp.setOwnerUuid(user.getUuid());
                            warp.setName(name);
                            warp.save();

                            // Send the player a message.
                            user.sendMessage("&7Created a new warp with name &f" + name + "&7.");
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
