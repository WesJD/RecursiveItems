package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * /sell command, opens the sell GUI or sells your hand if disabled
 *
 * @author Wesley Smith
 */
public class SellCommand extends AbstractCommand {

    public static final int CONFIRM_SLOT = 53;

    /**
     * @inheritDoc
     */
    public SellCommand(RecursiveItems main) {
        super(main, "recursiveitems.sell");
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCmd(Player player, String[] args) {
        final Inventory inventory = Bukkit.createInventory(player, 54, "Sell Items");
        player.setMetadata("sell_inventory", new FixedMetadataValue(super.main, inventory));

    }

}
