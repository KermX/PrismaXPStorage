package me.kermx.prismaXPStorage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class XPItemManager {

    private static final NamespacedKey XP_KEY = new NamespacedKey("prisma_xp_storage", "xp_amount");

    public static ItemStack createXPItem(int amount) {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("XP Book (" + amount + " Points)").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Right click to retrieve stored XP").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);

            meta.setEnchantmentGlintOverride(true);

            meta.setMaxStackSize(64);

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(XP_KEY, PersistentDataType.INTEGER, amount);

            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isXPItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(XP_KEY, PersistentDataType.INTEGER);
    }

    public static int getXPAmount(ItemStack item) {
        if (!isXPItem(item)) return 0;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(XP_KEY, PersistentDataType.INTEGER, 0);
    }
}
