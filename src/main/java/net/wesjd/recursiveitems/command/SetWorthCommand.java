package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.util.ParsingUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /setworth command, sets the worth of your hand or the stack supplied
 *
 * @author Daniel Gongora
 */
public class SetWorthCommand extends AbstractCommand {

    private static final String WORTH_SET = ChatColor.GREEN + "%s's%s worth has been set to $%s.";

    /**
     * @inheritDoc
     */
    public SetWorthCommand(RecursiveItems main) {
        super(main, "recursiveitems.setworth");
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCmd(Player player, String[] args) throws Exception {
        ItemStack toWorth;
        double worth;
        boolean isDefault;
        int index = 0;

        if(args.length == 0) {
            showUsage(player);
            return;
        }

        if (args[index].equalsIgnoreCase("default")) {//if they want the default, increase the index, and mark it was default
            index++;
            isDefault = true;
        } else isDefault = false;

        if (args.length - index == 2) { //item was supplied
            player.sendMessage("You fucked up: \""+args[index]+"\"");
            toWorth = ParsingUtils.getStackFromArg(args[index]);
        } else toWorth = player.getItemInHand();

        if (toWorth == null) {
            showUsage(player);
            return;
        }
        worth = Double.parseDouble(args[index + 1]);

        String extra = "";

        if (isDefault) {
            super.main.getEngine().setDefaultWorth(toWorth, worth);
            extra = " default"; //make it say "..GRASS's default worth has been.." instead of "..GRASS's worth has been.."
        } else super.main.getEngine().setWorth(toWorth, worth);

        player.sendMessage(String.format(WORTH_SET,
                toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : ""),
                extra,
                worth)); //tell them a beautiful message

        /*if (args.length <= 2) {
            final ItemStack hand = player.getItemInHand();
            if (hand != null) toWorth = hand;
            else {
                player.sendMessage(ChatColor.RED + "Usage: /setworth [default] [material[:dataValue]] <worth>");
                return;
            }
            worth = Double.parseDouble(args[0]);
        } else if (args.length < 4) {
            toWorth = ParsingUtils.getStackFromArg(args[0]);
            worth = Double.parseDouble(args[1]);
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments.");
            return;
        }

        super.main.getEngine().setWorth(toWorth, worth);
        player.sendMessage(ChatColor.GREEN.toString() + toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : "") +
                "'s worth has been set to $" + worth + ".");
*/
    }

    private void showUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /setworth [default] [material[:dataValue]] <worth>");
    }

}
