package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.util.ParsingUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /worth command, finds the worth of your hand or the stack supplied
 *
 * @author Wesley Smith
 */
public class WorthCommand extends AbstractCommand {

    /**
     * @inheritDoc
     */
    public WorthCommand(RecursiveItems main) {
        super(main);
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
                player.sendMessage(ChatColor.RED + "Usage: /worth <material[:dataValue]>");
                return;
            }
        } else if (args.length < 2) {
            toWorth = ParsingUtils.getStackFromArg(args[0]);
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "A " + toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : "") +
                " is worth " + super.plugin.getEconomy().format(super.plugin.getEngine().getWorth(toWorth)) + ".");
    }

}
