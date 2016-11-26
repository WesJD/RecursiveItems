package net.wesjd.recursiveitems.command;

import net.wesjd.recursiveitems.RecursiveItems;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * An AbstractCommand for abstract people
 *
 * @author Wesley Smith
 */
public abstract class AbstractCommand implements CommandExecutor {

    /**
     * Our local main class instance, accessible by implemented classes
     */
    protected final RecursiveItems main;
    /**
     * The permission for this command
     */
    private final String permission;

    /**
     * Creates an {@link AbstractCommand}
     *
     * @param main The main class
     * @param permission The permission for this command
     */
    public AbstractCommand(RecursiveItems main, String permission) {
        this.main = main;
        this.permission = permission;
    }

    /**
     * Called when the set command is executed
     *
     * @param player The player who ran the command
     * @param args   The command arguments
     * @throws Exception to handle exceptions
     */
    public abstract void onCmd(Player player, String[] args) throws Exception;

    /**
     * @inheritDoc
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if(permission == null || player.hasPermission(permission)) {
                try {
                    onCmd(player, args);
                } catch (Exception ex) {
                    player.sendMessage(ChatColor.RED + ex.getClass().getSimpleName() + ": " + ex.getMessage() + ".");
                }
            } else player.sendMessage(ChatColor.RED + "You need the permission " + permission + " to execute this command!");
        } else sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
        return true;
    }

}
