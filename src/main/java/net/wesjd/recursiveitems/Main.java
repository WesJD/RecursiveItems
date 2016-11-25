package net.wesjd.recursiveitems;

import net.milkbowl.vault.economy.Economy;
import net.wesjd.recursiveitems.command.SellCommand;
import net.wesjd.recursiveitems.command.SetWorthCommand;
import net.wesjd.recursiveitems.command.WorthCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * The main class
 * @author Wesley Smith, MilkBowl
 */
public class Main extends JavaPlugin {

    /**
     * Vault Economy instance, provided in {@link Main#setupEconomy()}
     */
    private Economy economy;
    /**
     * The ItemEngine instance to be used throughout
     */
    private ItemEngine engine;

    /**
     * @inheritDoc
     */
    @Override
    public void onEnable() {
        if(setupEconomy()) {
            engine = new ItemEngine(this);
            getCommand("sell").setExecutor(new SellCommand(this));
            getCommand("setworth").setExecutor(new SetWorthCommand(this));
            getCommand("worth").setExecutor(new WorthCommand(this));
        } else {
            getLogger().log(Level.SEVERE, "Unable to hook Vault economy, disabling plugin.");
            getPluginLoader().disablePlugin(Bukkit.getPluginManager().getPlugin("RecursiveItems"));
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDisable() {
        engine.cleanup();
    }

    /**
     * Hook into Vault economy
     * @return Weather the hook was successful
     */
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider != null) economy = economyProvider.getProvider();
        return (economy != null);
    }

    /**
     * Get the Economy instace
     * @return The instance
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Get the ItemEngine instance
     * @return The instance
     */
    public ItemEngine getEngine() {
        return engine;
    }

}
