package com.github.cozyplugins.cozywarps;

import com.github.cozyplugins.cozylibrary.CozyPlugin;
import com.github.cozyplugins.cozywarps.command.WarpsCommand;
import com.github.smuddgge.squishyconfiguration.ConfigurationFactory;
import com.github.smuddgge.squishyconfiguration.interfaces.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the main class.
 */
public final class CozyWarps extends CozyPlugin {

    private static @SuppressWarnings("all")
    @NotNull CozyWarps instance;

    private @SuppressWarnings("all")
    @NotNull Configuration storage;

    @Override
    public boolean enableCommandDirectory() {
        return true;
    }

    @Override
    public void onCozyEnable() {

        // Create the instance of the storage configuration.
        this.storage = ConfigurationFactory.YAML.create(this.getDataFolder(), "storage");
        this.storage.load();

        // Initialise the instance getter.
        CozyWarps.instance = this;

        // Add all the command types.
        this.addCommandType(new WarpsCommand());
    }

    /**
     * Used to get a warp from storage given its
     * unique identifier.
     *
     * @param identifier The warps unique identifier.
     * @return The optional warp.
     */
    public @NotNull Optional<Warp> getWarp(@NotNull UUID identifier) {
        if (this.storage.getKeys().contains(identifier.toString())) {
            return Optional.of(
                    new Warp(identifier).convert(this.storage.getSection(identifier.toString()))
            );
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
        for (String key : this.storage.getKeys()) {
            list.add(new Warp(UUID.fromString(key)).convert(this.storage.getSection(key)));
        }

        return list;
    }

    /**
     * Used to update or insert a warp in storage.
     *
     * @param warp The instance of the warp to update or insert.
     * @return This instance.
     */
    public @NotNull CozyWarps updateWarp(@NotNull Warp warp) {
        this.storage.set(warp.getIdentifier().toString(), warp.convert().getMap());
        this.storage.save();
        return this;
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
