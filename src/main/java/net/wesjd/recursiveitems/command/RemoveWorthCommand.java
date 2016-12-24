package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.util.ParsingUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /removeworth command, removes the worth of an ItemStack
 *
 * @author Daniel Gongora
 */
public class RemoveWorthCommand extends AbstractCommand {

    /**
     * @inheritDoc
     */
    public RemoveWorthCommand(RecursiveItems plugin) {
        super(plugin, "recursiveitems.removeworth");
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCmd(Player player, String[] args) throws Exception {
        ItemStack toWorth;
        if (args.length == 0) {
            final ItemStack hand = player.getItemInHand();
            if (hand != null) toWorth = hand;
            else {
                player.sendMessage(ChatColor.RED + "Usage: /removeworth [default] [material[:dataValue]]");
                return;
            }
        } else if (args.length < 2) {
            toWorth = ParsingUtils.getStackFromArg(args[0]);
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments.");
            return;
        }

        super.main.getEngine().removeWorth(toWorth);
        player.sendMessage(ChatColor.GREEN.toString() + toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : "") + "'s worth has been removed.");
    }

}
