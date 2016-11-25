package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * An AbstractCommand for abstract people
 * @author Wesley Smith
 */
public abstract class AbstractCommand implements CommandExecutor {

    /**
     * Our local main class instance, accessible by implemented classes
     */
    protected final Main main;

    /**
     * Creates an {@link AbstractCommand}
     * @param main The main class
     */
    public AbstractCommand(Main main) {
        this.main = main;
    }

    /**
     * Called when the set command is executed
     * @param player The player who ran the command
     * @param args The command arguments
     * @throws Exception to handle exceptions
     */
    public abstract void onCmd(Player player, String[] args) throws Exception;

    /**
     * @inheritDoc
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            final Player player = (Player) sender;
            try {
                onCmd(player, args);
                return true;
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        } else sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
        return false;
    }

}
