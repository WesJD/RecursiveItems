package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.util.ParsingUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RemoveWorthCommand extends AbstractCommand {

    public RemoveWorthCommand(RecursiveItems plugin) {
        super(plugin);
    }

    @Override
    public void onCmd(Player player, String[] args) throws Exception {
        ItemStack toWorth;
        if (args.length == 0) {
            final ItemStack hand = player.getItemInHand();
            if (hand != null) toWorth = hand;
            else {
                player.sendMessage(ChatColor.RED + "Usage: /removeworth [material[:dataValue]]");
                return;
            }
        } else if (args.length < 2) {
            toWorth = ParsingUtils.getStackFromArg(args[0]);
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments.");
            return;
        }

        super.plugin.getEngine().removeWorth(toWorth);
        player.sendMessage(ChatColor.GREEN.toString() + toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : "") + "'s worth has been removed.");
    }
}
