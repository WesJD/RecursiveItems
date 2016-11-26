package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /reload command, reloads the config and cache
 *
 * @author Wesley Smith
 */
public class ReloadCommand extends AbstractCommand {

    /**
     * @inheritDoc
     */
    public ReloadCommand(RecursiveItems main) {
        super(main, "recursiveitems.reload");
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCmd(Player player, String[] args) throws Exception {
        super.main.getEngine().reload();
        player.sendMessage(ChatColor.GREEN + "Config and cache reloaded.");
    }

}
