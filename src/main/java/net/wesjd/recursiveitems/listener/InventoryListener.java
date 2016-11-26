package net.wesjd.recursiveitems.listener;

import net.wesjd.recursiveitems.RecursiveItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class InventoryListener implements Listener {

    private final RecursiveItems main;

    public InventoryListener(RecursiveItems main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final Optional<Inventory> possibleInventory = getSellInventory(player);
        if(possibleInventory.isPresent() && e.getInventory().equals(possibleInventory.get())) //TODO - check if slot equals SellCommand.CONFIRM_SLOT
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        final Optional<Inventory> possibleInventory = getSellInventory(player);
        if(possibleInventory.isPresent() && e.getInventory().equals(possibleInventory.get())) player.removeMetadata("sell_inventory", main);
    }

    private Optional<Inventory> getSellInventory(Player player) {
        if(player.hasMetadata("sell_inventory")) return Optional.of((Inventory) player.getMetadata("sell_inventory").get(0).value());
        else return Optional.empty();
    }

}
