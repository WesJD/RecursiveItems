package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.util.ParsingUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /removeworth command, removes the worth of an ItemStack
 *
 * @author Daniel Gongora, Wesley Smith
 */
public class RemoveWorthCommand extends AbstractCommand {

    /**
     * Format used when sending a message to the player in {@link RemoveWorthCommand#onCmd(Player, String[])}
     */
    private static final String WORTH_REMOVE_FORMAT = ChatColor.GREEN + "%s's%s worth has been removed.";

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
        final boolean isDefault = args.length > 0 && args[0].equalsIgnoreCase("default");
        final int materialIndex = isDefault ? 1 : 0;

        ItemStack toWorth;
        if (args.length - materialIndex == 1) //item was supplied
            toWorth = ParsingUtils.getStackFromArg(args[materialIndex]);
        else toWorth = player.getItemInHand();

        if (toWorth == null || toWorth.getType() == Material.AIR) showUsage(player);
        else {
            if(isDefault) super.main.getEngine().removeDefaultWorth(toWorth);
            else super.main.getEngine().removeWorth(toWorth);
            player.sendMessage(String.format(
                    WORTH_REMOVE_FORMAT,
                    toWorth.getType() + (toWorth.getDurability() > 0 ? ":" + toWorth.getDurability() : ""),
                    (isDefault ? " default" : "")
            ));
        }
    }

    private void showUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /removeworth [default] [material[:dataValue]]");
    }

}
