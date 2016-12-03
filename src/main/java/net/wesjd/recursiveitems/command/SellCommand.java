package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.gui.SellingInventory;
import org.bukkit.entity.Player;

/**
 * /sell command, opens the sell GUI or sells your hand if disabled
 *
 * @author Wesley Smith
 */
public class SellCommand extends AbstractCommand {


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
        new SellingInventory(main, player);

    }

}
