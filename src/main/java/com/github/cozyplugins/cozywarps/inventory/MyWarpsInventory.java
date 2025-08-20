package com.github.cozyplugins.cozywarps.inventory;

import com.github.cozyplugins.cozylibrary.inventory.InventoryInterface;
import com.github.cozyplugins.cozylibrary.inventory.InventoryItem;
import com.github.cozyplugins.cozylibrary.inventory.action.action.ClickAction;
import com.github.cozyplugins.cozylibrary.user.PlayerUser;
import com.github.cozyplugins.cozywarps.CozyWarps;
import com.github.cozyplugins.cozywarps.Warp;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents my warp's inventory.
 * Shows a player's warps with an option to edit them, with paging.
 */
public class MyWarpsInventory extends InventoryInterface {

    private final @NotNull UUID ownerUuid;
    private int page = 0;

    // Item slots to use per page: 7 on row 2 (10–16) and 8 on row 3 (18–25)
    private static final int[] PAGE_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
    };

    public MyWarpsInventory(@NotNull UUID ownerUuid) {
        super(54, "&f₴₴₴₴₴₴₴₴홮");
        this.ownerUuid = ownerUuid;
    }

    @Override
    protected void onGenerate(PlayerUser player) {

        clearPageContents();

        // Back to main warps menu
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lBack")
                .setLore("&7Click to go back to the warps menu.")
                .addSlot(45)
                .addAction((ClickAction) (user, type, inventory) -> {
                    new WarpsInventory().open(user.getPlayer());
                })
        );

        // Help
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

        // Collect and sort the owner's warps once for this render
        List<Warp> warps = new ArrayList<>(CozyWarps.getInstance().getAllWarps(this.ownerUuid));
        Collections.sort(warps);

        int maxPageIndex = getMaxPageIndex(warps.size());
        if (page > maxPageIndex) page = maxPageIndex; // clamp if needed

        // Prev page
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lPrevious Page")
                .setLore("&7Click to go back a page.",
                        "&7",
                        "&fPage &a" + (page + 1) + "&7/&a" + Math.max(1, maxPageIndex + 1))
                .addSlot(50)
                .addAction((ClickAction) (user, type, inventory) -> {
                    if (page > 0) {
                        page -= 1;
                        this.onGenerate(player);
                    }
                })
        );

        // Next page
        this.setItem(new InventoryItem()
                .setMaterial(Material.PINK_STAINED_GLASS_PANE)
                .setCustomModelData(1)
                .setName("&a&lNext Page")
                .setLore("&7Click to go forward a page.",
                        "&7",
                        "&fPage &a" + (page + 1) + "&7/&a" + Math.max(1, maxPageIndex + 1))
                .addSlot(51)
                .addAction((ClickAction) (user, type, inventory) -> {
                    if (page < maxPageIndex) {
                        page += 1;
                        this.onGenerate(player);
                    }
                })
        );

        // --- Page contents ---
        addWarpItemsPaged(warps);
    }

    private void addWarpItemsPaged(List<Warp> warps) {
        final int perPage = PAGE_SLOTS.length;
        final int start = page * perPage;
        final int end = Math.min(start + perPage, warps.size());

        for (int i = start, slotIndex = 0; i < end; i++, slotIndex++) {
            final Warp warp = warps.get(i);

            InventoryItem item = warp.createInventoryItem();
            item.addLore("&7");
            item.addLore("&eClick to edit this warp.");
            item.addSlot(PAGE_SLOTS[slotIndex]);
            item.addAction((ClickAction) (user, type, inventory) ->
                    new WarpEditorInventory(warp.getIdentifier()).open(user.getPlayer())
            );

            this.setItem(item);
        }
    }

    private int getMaxPageIndex(int total) {
        if (total <= 0) return 0;
        return (total - 1) / PAGE_SLOTS.length;
    }

    private void clearPageContents() {
        for (int i = 0; i < 45; i++) {
            // Setting AIR (null under the hood) clears the slot in Bukkit.
            this.setItem(new InventoryItem()
                    .setMaterial(Material.AIR)
                    .addSlot(i));
        }
    }
}
