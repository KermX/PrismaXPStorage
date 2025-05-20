package me.kermx.prismaXPStorage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class XPStoreCommand implements CommandExecutor, TabCompleter {
    private final PrismaXPStorage plugin;
    private final List<String> commonXPValues = Arrays.asList("max", "315", "1395", "5345");
    private final int MAX_BOOKS = 64;

    public XPStoreCommand(PrismaXPStorage plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        if (!sender.hasPermission("prisma_xp_storage.xpstore")) {return true;}

        if (args.length < 1 || args.length > 2) {
            player.sendMessage("Usage: /xpstore <amount|max> [quantity]");
            return true;
        }

        int playerXp = XPUtils.getTotalExperience(player);
        int amount;

        // Parse the amount of XP per book
        if (args[0].equalsIgnoreCase("max")) {
            amount = playerXp;
            if (amount <= 0) {
                player.sendMessage(Component.text("You don't have any XP to store!", NamedTextColor.RED));
                return true;
            }
        } else {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Invalid amount! Use a number or 'max'", NamedTextColor.RED));
                return true;
            }

            if (amount <= 0) {
                player.sendMessage(Component.text("Amount must be positive!", NamedTextColor.RED));
                return true;
            }
        }

        // Parse the quantity of books
        int quantity = 1;
        if (args.length == 2) {
            try {
                quantity = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Invalid quantity! Use a number.", NamedTextColor.RED));
                return true;
            }

            if (quantity <= 0) {
                player.sendMessage(Component.text("Quantity must be positive!", NamedTextColor.RED));
                return true;
            }

            if (quantity > MAX_BOOKS) {
                player.sendMessage(Component.text("Maximum quantity is " + MAX_BOOKS + " books!", NamedTextColor.YELLOW));
                quantity = MAX_BOOKS;
            }
        }

        // Calculate total XP needed
        int totalXpNeeded = amount * quantity;

        // Check if player has enough XP
        if (playerXp < totalXpNeeded) {
            // If player doesn't have enough for requested amount, calculate max possible books
            int maxPossibleBooks = playerXp / amount;
            if (maxPossibleBooks <= 0) {
                player.sendMessage(Component.text("You don't have enough XP! You only have " + playerXp + " XP.", NamedTextColor.RED));
                return true;
            }

            quantity = maxPossibleBooks;
            totalXpNeeded = amount * quantity;
            player.sendMessage(Component.text("You only have XP for " + quantity + " books. Creating that many instead.", NamedTextColor.YELLOW));
        }

        // Check if player has at least one free inventory slot
        // Since books stack, we only need one free slot
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Component.text("You need at least one free inventory slot!", NamedTextColor.RED));
            return true;
        }

        // Remove XP from player
        XPUtils.setTotalExperience(player, playerXp - totalXpNeeded);

        // Create and give XP books (they'll automatically stack)
        ItemStack xpItem = XPItemManager.createXPItem(amount);
        xpItem.setAmount(quantity);
        player.getInventory().addItem(xpItem);

        if (quantity == 1) {
            player.sendMessage(Component.text("You have stored " + amount + " XP in a book!", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("You have stored " + totalXpNeeded + " XP in " + quantity + " books! (" + amount + " XP each)", NamedTextColor.GREEN));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            final String input = args[0].toLowerCase();
            return commonXPValues.stream()
                    .filter(value -> value.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            final String input = args[1].toLowerCase();
            List<String> quantities = Arrays.asList("1", "5", "10", "32", "64");
            return quantities.stream()
                    .filter(value -> value.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}