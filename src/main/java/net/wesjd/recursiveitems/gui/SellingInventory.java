package net.wesjd.recursiveitems.gui;

import net.wesjd.recursiveitems.RecursiveItems;
import net.wesjd.recursiveitems.util.ItemBuilder;
import net.wesjd.recursiveitems.util.ParsingUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Inventory to allow players to easily sell a large quantity of items
 *
 * @author Daniel Gongora
 */
public class SellingInventory implements Listener {

    /**
     * Indexes for items and constant inventory size
     */
    private static final int INVENTORY_SIZE = 54,
            SELL_AGREE_INDEX = 1,
            SELL_DISPLAY_INDEX = 4,
            SELL_CANCEL_INDEX = 7,
            NO_ITEMS_INDEX = 22;

    /**
     * Preset items utilized in the creation and rendering of the inventory
     */
    private static final ItemStack SELL_AGREE_ICON = new ItemBuilder(Material.EMERALD_BLOCK).name(ChatColor.GREEN + "Sell Inventory").build(),

    SELL_CANCEL_ICON = new ItemBuilder(Material.REDSTONE_BLOCK).name(ChatColor.RED + "Cancel Sell").build(),

    NO_ITEMS_YET = new ItemBuilder(Material.APPLE).name(ChatColor.YELLOW + "No items yet!")
            .lore(ChatColor.GRAY + "You can place items here").lore(ChatColor.GRAY + "and hover over the paper to")
            .lore(ChatColor.GRAY + "see the total value of what you're selling").build(),

    SPACE_FILLER = new ItemBuilder(Material.STAINED_GLASS_PANE).data(15).build();

    /**
     * Display format used for {@link SellingInventory#SELL_DISPLAY_INDEX} item creation
     */
    private static final String SELL_DISPLAY_FORMAT = ChatColor.GRAY + "Your total worth is: " + ChatColor.YELLOW + "%s";

    /**
     * Main instance of our plugin
     */
    private final RecursiveItems main;

    /**
     * Inventory the {@link Player} currently has open
     */
    private final Inventory inventory;

    /**
     * Stored {@link UUID} of the player who opened the inventory
     */
    private final UUID player;

    /**
     * {@link ItemStack[]} containing all the items the {@link Player}
     * will be selling.
     */
    private final ItemStack[] stored;

    /**
     * Creates a new SellingInventory, which automatically opens
     *
     * @param main   Instance of our main class
     * @param player {@link Player} to open the inventory for
     */
    public SellingInventory(RecursiveItems main, Player player) {
        this.main = main;
        this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE, "Sell Items");
        this.stored = new ItemStack[INVENTORY_SIZE - 9];
        this.player = player.getUniqueId();

        try {
            initializeTopRow();
            player.openInventory(inventory);
        } catch (Exception ex) {
            ex.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error while processing your request.");
        }
    }

    /**
     * Called to register events
     */
    private void register() {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    /**
     * Called when the player closes the inventory or leaves
     * <br />
     * Unregisters all listeners and removes the {@link Player}'s
     * metadata, returning items the player has placed in
     * the inventory to their inventory
     */
    private void handleClosing() {
        HandlerList.unregisterAll(this);
        getPlayer().ifPresent(p -> Arrays.stream(stored).filter(Objects::nonNull).forEach(stack -> giveOrDrop(p, stack)));
    }


    /**
     * Called when the player clicks on the {@link SellingInventory#SELL_AGREE_ICON} button
     */
    private void handleSell() {
        getPlayer().ifPresent(p -> {
            try {
                main.getEconomy().depositPlayer(p, main.getEngine().getWorth(stored)); //give the player their money

                for (int i = 0; i < stored.length; i++) stored[i] = null; //prevent duplication in rare cases
            } catch (Exception ex) {
                ex.printStackTrace();
                p.closeInventory();
                p.sendMessage(ChatColor.RED + "There was an error while processing your request.");
            }
        });
    }

    /**
     * Called when the player clicks on the {@link SellingInventory#SELL_CANCEL_ICON} button
     */
    private void handleCancel() {
        getPlayer().ifPresent(HumanEntity::closeInventory); //just close, handleClosing() will handle giving back items.
    }

    /**
     * Initializes top row which is never modified after
     * initialization. Contains useful buttons for the player
     *
     * @throws Exception if an exeception was thrown
     */
    private void initializeTopRow() throws Exception {
        for (int i = 0; i < 9; i++) {
            switch (i) {
                case SELL_AGREE_INDEX:
                    inventory.setItem(i, SELL_AGREE_ICON);
                    break;
                case SELL_DISPLAY_INDEX:
                    updateSellIcon();
                    break;
                case SELL_CANCEL_INDEX:
                    inventory.setItem(i, SELL_CANCEL_ICON);
                    break;
                default:
                    inventory.setItem(i, SPACE_FILLER);
                    break;
            }
        }
    }

    /**
     * Generates and sets the {@link SellingInventory#SELL_DISPLAY_INDEX}
     * to an updated icon
     *
     * Called whenever inventory initialized or inventory re-rendered
     * @throws Exception if an exception was thrown
     */
    private void updateSellIcon() throws Exception {
        inventory.setItem(SELL_DISPLAY_INDEX, generateSellIcon());
    }

    /**
     * Creates a new sell icon which displays the
     * current amount of money the items being
     * sold are worth
     *
     * @return {@link ItemStack} generated
     * @throws Exception if an exception was thrown
     */
    private ItemStack generateSellIcon() throws Exception {
        return new ItemBuilder(Material.PAPER).name(String.format(SELL_DISPLAY_FORMAT, main.getEngine().getWorth(stored))).build();
    }

    /**
     * Resets and re-renders selling inventory,
     * placing a "no items yet" item if the player
     * has not placed any items in the selling inventory
     */
    private void displayInventoryItems() {
        try {
            resetInventory();
            updateSellIcon(); //update due to possible inventory change
            boolean hasRendered = false;
            for (int i = 9; i <= INVENTORY_SIZE; i++) {
                ItemStack render = stored[i - 9]; //subtract nine since first row is 9, and stored items start at index 0

                inventory.setItem(i, render);
                if (render != null) hasRendered = true;
            }
            if (!hasRendered) inventory.setItem(NO_ITEMS_INDEX, NO_ITEMS_YET); //no items are placed in inventory!
        } catch (Exception ex) {
            ex.printStackTrace();
            getPlayer().ifPresent(p -> {
                p.closeInventory();
                p.sendMessage(ChatColor.RED + "There was an error while processing your request.");
            });
        }
    }

    /**
     * Resets the inventory containing the items the player is selling
     */
    private void resetInventory() {
        for (int i = 9; i <= INVENTORY_SIZE; i++) inventory.setItem(i, null);
    }

    /**
     * Finds an open slot for an {@link ItemStack}
     *
     * @param stack {@link ItemStack} to find a slot for
     * @return {@link Optional<Integer>} of the index found
     */
    private Optional<Integer> getAvailableSlotFor(ItemStack stack) {
        for (int i = 0; i < stored.length; i++) {
            ItemStack item = stored[i];
            if (item == null) return Optional.of(i); //found an available slot
            else if (item.isSimilar(stack)) return Optional.of(i); //check if the two can be grouped together
        }
        return Optional.empty(); //no space for item
    }

    /**
     * Adds an {@link ItemStack} to internal storage
     *
     * @param input {@link ItemStack} being added
     */
    private void addItem(ItemStack input) {
        getAvailableSlotFor(input).ifPresent(i -> {
            ItemStack at = stored[i];
            if (at == null) stored[i] = input; //slot was empty, just place it there
            else at.setAmount(at.getAmount() + input.getAmount()); //already item there, but can be merged
            displayInventoryItems(); //re-render inventory
        });
    }

    /**
     * Removes an {@link ItemStack} stored internally
     *
     * @param index Index of {@link ItemStack} in {@link SellingInventory#stored}
     */
    private void removeItem(int index) {
        ItemStack addToPlayer = stored[index];
        if (addToPlayer != null) {//if we are actually removing an item
            getPlayer().ifPresent(p -> giveOrDrop(p, addToPlayer)); //give them the item back
            displayInventoryItems(); //re-render inventory
        }

    }

    /**
     * Attempts to add an {@link ItemStack} to a {@link Player}'s inventory
     * Drops the {@link ItemStack} if it fails.
     *
     * @param player Player to attempt to add an {@link ItemStack} to
     * @param stack  {@link ItemStack} being added
     */
    private void giveOrDrop(Player player, ItemStack stack) {
        player.getInventory().addItem(stack).values() //give the items back to the player
                .forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i)); //drop what doesn't fit
    }

    /**
     * Gets the {@link Player} who opened this inventory
     *
     * @return An {@link Optional<Player>} who opened the inventory
     */
    private Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(player));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        getPlayer().filter(p -> player.equals(e.getWhoClicked().getUniqueId())) //if our opener clicked
                .filter(p -> e.getInventory().equals(inventory) && e.getSlotType() == InventoryType.SlotType.CONTAINER) //and they clicked inside our inventory
                .ifPresent(p -> { //do
                    e.setCancelled(true); //cancel movement

                    switch (e.getRawSlot()) {
                        case SELL_AGREE_INDEX: //selling the items!
                            handleSell();
                            break;
                        case SELL_CANCEL_INDEX: //nope, nevermind
                            handleCancel();
                            break;

                        default:
                            if (e.getRawSlot() > 8) { //make sure they aren't clicking space fillers
                                if (e.getCurrentItem() != null)  //placing an item in
                                    addItem(e.getCurrentItem());
                                else removeItem(e.getRawSlot()); //removing an item
                            }
                            break;
                    }
                });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        getPlayer().filter(p -> player.equals(e.getWhoClicked().getUniqueId())) //if our opener clicked
                .filter(p -> e.getInventory().equals(inventory))
                .ifPresent(p -> { //and they clicked inside our inventory
                    e.setCancelled(true);
                    e.getNewItems().values().forEach(this::addItem); //add some items
                });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().getUniqueId().equals(player)) e.getPlayer().closeInventory(); //triggers InventoryCloseEvent
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        handleClosing();
    }

}
