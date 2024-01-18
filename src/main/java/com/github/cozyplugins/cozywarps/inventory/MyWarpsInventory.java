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

package com.github.cozyplugins.cozywarps.inventory;

import com.github.cozyplugins.cozylibrary.inventory.InventoryInterface;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozywarps.CozyWarps;
import com.github.cozyplugins.cozywarps.Warp;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents my warp's inventory.
 * Shows a player's warps with an option to edit them.
 */
public class MyWarpsInventory extends InventoryInterface {

    private final @NotNull UUID ownerUuid;

    /**
     * Used to create an instance of the player warp's in
     * an interactive inventory.
     *
     * @param ownerUuid The warp's owner.
     */
    public MyWarpsInventory(@NotNull UUID ownerUuid) {
        super(54, "&f₴₴₴₴₴₴₴₴▶");

        this.ownerUuid = ownerUuid;
    }

    @Override
    protected void onGenerate(PlayerUser player) {

        // Back button.
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lBack")
                .setLore("&7Click to go back to the warps menu.")
                .addSlot(45)
                .addAction((ClickAction) (user, type, inventory) -> {
                    user.sendMessage("&7Opening warps menu...");
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
                        "&a/warps edit <name> &fTo edit a warp.",
                        "&7",
                        "&7Visits are counted as every unique player",
                        "&7visiting your warp every hour.")
                .addSlot(46, 47, 48)
        );

        // Add warps.
        this.addWarpItems(player);
    }

    /**
     * Used to add the warp items.
     *
     * @param player The instance of the player.
     */
    public void addWarpItems(@NotNull PlayerUser player) {

        int slot = 10;

        for (Warp warp : CozyWarps.getInstance().getAllWarps(player.getUuid())) {

            InventoryItem item = warp.createInventoryItem();
            item.addLore("&7");
            item.addLore("&eClick to edit this warp.");
            item.addSlot(slot);
            item.addAction((ClickAction) (user, type, inventory) -> {
               // TODO
            });

            // Add the item to the inventory.
            this.setItem(item);

            if (slot > 25) return;
            if (slot == 16) slot = 18;
            slot++;
        }
    }
}
