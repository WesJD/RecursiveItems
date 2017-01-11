package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.util.ParsingUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /setworth command, sets the worth of your hand or the stack supplied
 *
 * @author Daniel Gongora, Wesley Smith
 */
public class SetWorthCommand extends AbstractCommand {

    private static final String WORTH_SET = ChatColor.GREEN + "%s's%s worth has been set to $%s.";

    /**
     * Format used when sending a message to the player in {@link SetWorthCommand#onCmd(Player, String[])}
     */
    private final String WORTH_SET_FORMAT = ChatColor.GREEN + "%s's%s worth has been set to $%s.";

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
        if(args.length == 0) {
            showUsage(player);
            return;
        }

        final boolean isDefault = args[0].equalsIgnoreCase("default");
        int stackIndex = isDefault ? 1 : 0;

        if(args.length - stackIndex > 0) { //they supplied a worth
            ItemStack toWorth;
            if (args.length - stackIndex == 2) //item was supplied
                toWorth = ParsingUtils.getStackFromArg(args[stackIndex]);
            else {
                toWorth = player.getItemInHand();
                stackIndex--; //there is none
            }

            if (toWorth == null || toWorth.getType() == Material.AIR) showUsage(player);
            else {
                toWorth = toWorth.clone();
                toWorth.setAmount(1);
                final double worth = Double.parseDouble(args[stackIndex + 1]);

                if (isDefault) super.main.getEngine().setDefaultWorth(toWorth, worth);
                else super.main.getEngine().setWorth(toWorth, worth);

                player.sendMessage(String.format(
                        WORTH_SET_FORMAT,
                        toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : ""),
                        (isDefault ? " default" : ""),
                        worth
                ));
            }
        } else showUsage(player);
    }

    private void showUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /setworth [default] [material[:dataValue]] <worth>");
    }

}
