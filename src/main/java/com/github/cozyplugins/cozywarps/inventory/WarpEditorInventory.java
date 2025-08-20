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

package com.github.cozyplugins.cozywarps.inventory;

import com.github.cozyplugins.cozylibrary.inventory.InventoryInterface;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.ActionResult;
import com.github.cozyplugins.cozylibrary.inventory.action.action.*;
import com.github.cozyplugins.cozylibrary.inventory.inventory.AnvilInputInventory;
import com.github.cozyplugins.cozylibrary.item.CozyItem;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozywarps.CozyWarps;
import com.github.cozyplugins.cozywarps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the warp editor.
 * Used to edit a warp.
 */
public class WarpEditorInventory extends InventoryInterface {

    private final @NotNull UUID warpIdentifier;

    /**
     * Used to create a warp editor inventory.
     *
     * @param warpIdentifier The warp's identifier that is being edited.
     */
    public WarpEditorInventory(@NotNull UUID warpIdentifier) {
        super(54, "&f₴₴₴₴₴₴₴₴☒");

        this.warpIdentifier = warpIdentifier;
    }

    @Override
    protected void onGenerate(PlayerUser player) {

        // Get the warp.
        Warp warp = this.getWarp().orElse(null);
        if (warp == null) {
            player.sendMessage("&7&l> &7Warp returned null, something went wrong.");
            return;
        }

        // Back button.
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lBack")
                .setLore("&7Click to go back to the list of your warps.")
                .addSlot(45)
                .addAction((ClickAction) (user, type, inventory) -> {
                    new WarpsInventory().open(user.getPlayer());
                })
        );

        // Help button.
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&b&lHelp")
                .setLore("&7Player warps let other players teleport",
                        "&7to locations you have chosen.",
                        "&7",
                        "&a/warps &fTo list all warps.",
                        "&a/warps create <name> &fTo create a warp.",
                        "&a/warps delete <name> &fTo delete a warp.",
                        "&a/warps ban <player> &fTo ban a player from your warps.",
                        "&a/warps unban <player> &fTo unban a player from your warps.",
                        "&7",
                        "&c&lPlease Note: &r&7Warps belong to the community, not the creators. ",
                        "&7Server staff may change warp management",
                        "&7if it is in the best interest of the community.",
                        "&7",
                        "&7Visits are counted as every unique player",
                        "&7visiting your warp every hour.")
                .addSlot(46, 47, 48)
        );

        // Icon.
        this.setItem(new InventoryItem()
                .setMaterial(warp.getMaterial())
                .setName("&f&lChange The Warp Icon")
                .setLore("&7Click me with another item",
                        "&7to change the icon!")
                .addSlot(13)
                .addAction(new ClickActionWithResult() {
                    @Override
                    public @NotNull ActionResult onClick(@NotNull PlayerUser user, ClickType type, @NotNull Inventory inventory, @NotNull ActionResult currentResult, int slot, @NotNull InventoryClickEvent event) {
                        if (event.getCursor() == null) return new ActionResult().setCancelled(true);
                        CozyItem item = new CozyItem(event.getCursor());

                        if (item.getMaterial() == Material.AIR) {
                            return new ActionResult().setCancelled(true);
                        }

                        // Set the material.
                        warp.setMaterial(item.getMaterial());
                        warp.save();

                        user.sendMessage("&7&l> &7Changed the warp icon to " + warp.getMaterial().name() + ".");
                        WarpEditorInventory.this.open(user.getPlayer());
                        return new ActionResult().setCancelled(true);
                    }
                })
        );

        // Name button.
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&6&lChange Warp Name")
                .setLore("&7Click to change the warps name.",
                        "&7",
                        "&aCurrently &a" + warp.getName())
                .addSlot(28, 29)
                .addAction(new AnvilValueAction()
                        .setAnvilTitle("&8&lWarp Name")
                        .setAction((value, user) -> {
                            if (value != null && !value.isEmpty()) {
                                warp.setName(value);
                                warp.save();
                                user.sendMessage("&7&l> &7Warp name changed to " + value + ".");
                            }
                            this.open(user.getPlayer());
                        })
                )
        );

        // Description button.
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&6&lChange Warp Description")
                .setLore("&7Click to change the warps description.",
                        "&7",
                        "&aCurrently &a" + warp.getDescription())
                .addSlot(30, 31)
                .addAction(new AnvilValueAction()
                        .setAnvilTitle("&8&lWarp Description")
                        .setAction((value, user) -> {
                            if (value != null) {
                                warp.setDescription(value);
                                warp.save();
                                user.sendMessage("&7&l> &7Warp description changed to \"" + value + "\".");
                            }
                            this.open(user.getPlayer());
                        })
                )
        );

        // Location button.
        this.setItem(new InventoryItem()
                .setMaterial(Material.COMPASS)
                .setCustomModelData(0)
                .setName("&6&lChange Warp Location")
                .setLore("&7Change the warp location to where",
                        "&7you are standing.")
                .addSlot(32)
                .addAction((ClickAction) (user, type, inventory) -> {
                    user.sendMessage("&7&l> &7Warps location has been changed.");
                    warp.setLocation(user.getPlayer().getLocation());
                    warp.save();
                    this.open(user.getPlayer());
                })
        );

        // Manager Button
        this.setItem(new InventoryItem()
                .setMaterial(Material.PLAYER_HEAD)
                .setCustomModelData(0)
                .setName("&c&lTransfer Management")
                .setLore("&7Click to change the warps manager",
                        "&7 you will not be refunded")
                .addSlot(33)
                .addAction(new AnvilValueAction()
                                .setAnvilTitle("New Warp Owner")
                        .setAction((value, user) -> {
                            if (value != null){
                                if(Bukkit.getPlayer(value) != null){
                                    UUID newManagerUuid = Bukkit.getOfflinePlayer(value).getUniqueId();
                                    warp.setManagerUuid(newManagerUuid);
                                    warp.save();
                                }
                                else{
                                    user.sendMessage(ChatColor.RED + "Player Not Valid or offline!");
                                }
                            }
                        })
                )
        );





        // Delete button.
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&c&lDelete Warp")
                .setLore("&7Click to delete the warp.",
                        "&7you will not be refunded.")
                .addSlot(34)
                .addAction(new ConfirmAction()
                        .setAnvilTitle("&8&lDelete " + warp.getName())
                        .setAbort(user -> {
                            user.sendMessage("&7&l> &7Aborted deleting warp.");
                            this.open(user.getPlayer());
                        })
                        .setConfirm(user -> {
                            CozyWarps.getInstance().removeWarp(user.getName(), warp.getName());
                            user.sendMessage("&7&l> &7Deleted warp " + warp.getName());
                            new MyWarpsInventory(user.getUuid()).open(user.getPlayer());
                        })
                )
        );
    }

    /**
     * Used to get the instance of the warp.
     *
     * @return The instance of the warp
     * as an optional.
     */
    public @NotNull Optional<Warp> getWarp() {
        return CozyWarps.getInstance().getWarp(this.warpIdentifier);
    }
}
