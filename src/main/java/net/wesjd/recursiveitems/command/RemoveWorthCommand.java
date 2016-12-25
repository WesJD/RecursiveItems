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

    private static final String WORTH_REMOVE = ChatColor.GREEN + "%s's%s worth has been removed.";

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
        boolean isDefault;
        int index = 0;

        if (args.length > 0 && args[index].equalsIgnoreCase("default")) {//if they want the default, increase the index, and mark it was default
            index++;
            isDefault = true;
        } else isDefault = false;

        player.sendMessage("THINGITNHITNG: "+(args.length - index));

        if (args.length - index == 1) { //item was supplied
            toWorth = ParsingUtils.getStackFromArg(args[index]);
            player.sendMessage("ayayaayaye");
        } else toWorth = player.getItemInHand();

        if (toWorth == null) {
            showUsage(player);
            return;
        }

        String extra = "";
        System.out.println("removing worth for: "+(toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : "")));

        if (isDefault) {
            super.main.getEngine().removeDefaultWorth(toWorth);
            extra = " default"; //make it say "..GRASS's default worth has been.." instead of "..GRASS's worth has been.."
        } else super.main.getEngine().removeWorth(toWorth);

        player.sendMessage(String.format(WORTH_REMOVE,
                toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : ""),
                extra)); //tell them a beautiful message
/*
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
        player.sendMessage(ChatColor.GREEN.toString() + toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : "") + "'s worth has been removed.");*/
    }

    private void showUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /removeworth [default] [material[:dataValue]]");
    }

}
