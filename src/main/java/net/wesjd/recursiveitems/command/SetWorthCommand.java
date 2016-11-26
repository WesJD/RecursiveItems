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
        if (args.length == 1) {
            final ItemStack hand = player.getItemInHand();
            if (hand != null) toWorth = hand;
            else {
                player.sendMessage(ChatColor.RED + "Usage: /setworth [material[:dataValue]] <worth>");
                return;
            }
            worth = Double.parseDouble(args[0]);
        } else if (args.length < 3) {
            toWorth = ParsingUtils.getStackFromArg(args[0]);
            worth = Double.parseDouble(args[1]);
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments.");
            return;
        }

        super.main.getEngine().setWorth(toWorth, worth);
        player.sendMessage(ChatColor.GREEN.toString() + toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : "") +
                "'s worth  has been set to $" + worth + ".");

    }

}
