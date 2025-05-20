package me.kermx.prismaXPStorage;

import org.bukkit.plugin.java.JavaPlugin;

public final class PrismaXPStorage extends JavaPlugin {

    @Override
    public void onEnable() {
        XPStoreCommand xpStoreCommand = new XPStoreCommand(this);

        this.getCommand("xpstore").setExecutor(xpStoreCommand);
        this.getCommand("xpstore").setTabCompleter(xpStoreCommand);

        getServer().getPluginManager().registerEvents(new XPItemListener(this), this);
    }

    @Override
    public void onDisable() { }
}
