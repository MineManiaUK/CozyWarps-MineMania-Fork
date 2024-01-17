package com.github.cozyplugins.cozywarps.command;

import com.github.cozyplugins.cozylibrary.command.command.CommandType;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandArguments;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandStatus;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandSuggestions;
import com.github.cozyplugins.cozylibrary.command.datatype.CommandTypePool;
import com.github.cozyplugins.cozylibrary.user.ConsoleUser;
import com.github.cozyplugins.cozylibrary.user.FakeUser;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozylibrary.user.User;
import com.github.cozyplugins.cozywarps.CozyWarps;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

/**
 * Represents the ban command.
 * Used to ban players from your warps.
 */
public class WarpsBanCommand implements CommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "ban";
    }

    @Override
    public @Nullable String getSyntax() {
        return "/[parent] [name] <name>";
    }

    @Override
    public @Nullable String getDescription() {
        return "Used to ban a player from your warps.";
    }

    @Override
    public @Nullable CommandTypePool getSubCommandTypes() {
        return null;
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {

        // Check if they have staff permissions.
        if (user.hasPermission("cozywarps.staff")) {

            return new CommandSuggestions()
                    .append(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList())
                    .append(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
        }

        return new CommandSuggestions().append(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
    }

    @Override
    public @Nullable CommandStatus onUser(@NotNull User user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {
        return null;
    }

    @Override
    public @Nullable CommandStatus onPlayer(@NotNull PlayerUser user, @NotNull ConfigurationSection section, @NotNull CommandArguments arguments) {

        // Check if they have staff permissions.
        if (user.hasPermission("cozywarps.staff")) {

            // Check if they have the correct number of arguments.
            if (arguments.getArguments().size() < 2) {
                user.sendMessage("&7Please select a warp owner and a player to ban.");
                return new CommandStatus();
            }

            final String ownerName = arguments.getArguments().get(0);
            final String playerName = arguments.getArguments().get(1);

            final UUID ownerUuid = Bukkit.getOfflinePlayer(ownerName).getUniqueId();
            final UUID playerUuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();

            CozyWarps.getInstance().banPlayer(playerUuid, ownerUuid);
            user.sendMessage("&7The player &f" + playerName + " &7has been baned from &f" + ownerName + "'s &7warps.");
            return new CommandStatus();
        }

        // Check for the correct number of arguments.
        if (arguments.getArguments().isEmpty() || arguments.getArguments().get(0).isEmpty()) {
            user.sendMessage("&7Please provide a name to ban from your warps.");
            return new CommandStatus();
        }

        final String playerName = arguments.getArguments().get(1);
        final UUID playerUuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();

        CozyWarps.getInstance().banPlayer(user.getUuid(), playerUuid);
        user.sendMessage("&7The player &f" + playerName + " &7has been banned from your warps.");
        return null;
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
