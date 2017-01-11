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
        super(main, "recursiveitems.worth");
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
                player.sendMessage(ChatColor.RED + "Usage: /worth [material[:dataValue]]");
                return;
            }
        } else if (args.length < 2) {
            toWorth = ParsingUtils.getStackFromArg(args[0]);
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments.");
            return;
        }

        final StringBuilder output = new StringBuilder()
                .append(ChatColor.GREEN.toString())
                .append(toWorth.getAmount())
                .append(" ")
                .append(toWorth.getType().toString());
        if(toWorth.getDurability() > 0) output.append(":").append(toWorth.getDurability());
        output.append(" is worth ").append(super.main.getEconomy().format(super.main.getEngine().getWorth(toWorth)));
        if(toWorth.getAmount() > 1) {
            final ItemStack singluar = toWorth.clone();
            singluar.setAmount(1);
            output.append(" (").append(super.main.getEconomy().format(super.main.getEngine().getWorth(singluar))).append(" each)");
        }
        player.sendMessage(output.append(".").toString());
    }

}
