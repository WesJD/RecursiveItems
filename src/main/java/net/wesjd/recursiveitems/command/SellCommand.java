package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.gui.SellingInventory;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

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
    public void onCmd(Player player, String[] args) throws Exception {
        if(args.length > 0) {
            player.sendMessage("lol"); //TODO - Handle type & amount args (search thru inventory)
        } else if(super.main.getConfig().getBoolean("use-sell-gui")) {
            new SellingInventory(super.main, player);
        } else {
            player.sendMessage("lol2"); //TODO - Handle hand (search thru inventory)
        }
    }

}
