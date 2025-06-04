package me.kermx.prismaXPStorage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class XPItemListener implements Listener {

    private final PrismaXPStorage plugin;

    public XPItemListener(PrismaXPStorage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (XPItemManager.isXPItem(item)) {
                event.setCancelled(true);

                if (event.getHand() == EquipmentSlot.OFF_HAND) {
                    player.sendMessage(Component.text("XP books can only be used from your main hand!", NamedTextColor.RED));
                    return;
                }

                int xpAmount = XPItemManager.getXPAmount(item);
                int booksToUse = 1;

                // If player is sneaking, use the entire stack
                if (player.isSneaking()) {
                    booksToUse = item.getAmount();
                    int totalXP = xpAmount * booksToUse;

                    XPUtils.addExperience(player, totalXP);
                    player.getInventory().setItemInMainHand(null);

                    player.sendMessage(Component.text("You retrieved " + totalXP + " XP from " + booksToUse + " books!", NamedTextColor.GREEN));
                } else {
                    // Original behavior for single book
                    XPUtils.addExperience(player, xpAmount);

                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }

                    player.sendMessage(Component.text("You retrieved " + xpAmount + " XP!", NamedTextColor.GREEN));
                }
            }
        }
    }
}