package net.wesjd.recursiveitems;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.TimeUnit;

/**
 * The Item Engine, where almost everything is handled
 *
 * @author Wesley Smith
 */
public class ItemEngine {

    /**
     * The main class, passed in the constructor
     */
    private final RecursiveItems main;
    /**
     * The {@link ItemStack} to worth cache, cleared on config reload
     */
    private LoadingCache<ItemStack, Double> itemWorth;

    /**
     * Creates a new {@link ItemEngine}, only accessible from the main class
     *
     * @param main The {@link RecursiveItems} class
     */
    ItemEngine(RecursiveItems main) {
        this.main = main;
        initializeCache();
    }

    /**
     * Resets/creates the cache
     */
    private void initializeCache() {
        itemWorth = CacheBuilder.newBuilder()
                .expireAfterAccess(main.getConfig().getLong("cache.expire-time-seconds"), TimeUnit.SECONDS)
                .maximumSize(main.getConfig().getInt("cache.max-size"))
                .build(new WorthCacheLoader());
    }

    /**
     * Recursively searches for the worth of this {@link ItemStack} based on it's ingredients
     *
     * @param stack The {@link ItemStack} to find the worth of
     * @return The item's worth
     * @throws NoWorthException if any base items involved in the crafting of this item do not have a defined price
     * @throws Exception        if the {@link LoadingCache} throws it
     */
    public double getWorth(ItemStack stack) throws Exception {
        return itemWorth.get(stack) * stack.getAmount();
    }

    /**
     * Recursively searches for the worth of this {@link ItemStack[]} based on it's ingredients
     *
     * @param stacks The {@link ItemStack[]} to find the worth of
     * @return The item's worth or 0 if none of the items have a defined price
     * @throws Exception if an item doesn't have a worth
     */
    public double getWorth(ItemStack... stacks) throws Exception {
        double total = 0;
        for (ItemStack stack : stacks) if (stack != null) total += getWorth(stack);
        return total;
    }

    /**
     * Set the default worth of an {@link ItemStack}
     *
     * @param stack The {@link ItemStack} to set the default worth of
     * @param value The default worth of the item
     */
    public void setDefaultWorth(ItemStack stack, double value) {
        main.getConfig().set("defined." + stack.getType() + ".default", value);
    }

    /**
     * Set the worth of an {@link ItemStack}({@link Material} and data value)
     *
     * @param stack The {@link ItemStack} to set the worth of
     * @param value The worth of the item
     */
    public void setWorth(ItemStack stack, double value) {
        main.getConfig().set("defined." + stack.getType() + "." + stack.getDurability(), value);
    }

    /**
     * Check to see if any worth exists for an {@link ItemStack}
     *
     * @param stack The {@link ItemStack} to check for
     * @return Weather the item has any worth
     */
    public boolean hasAnyWorth(ItemStack stack) {
        final ConfigurationSection section = main.getConfig().getConfigurationSection("defined." + stack.getType() + "." + stack.getDurability());
        if(section == null) return false;
        else return section.getKeys(false).isEmpty();
    }

    /**
     * Removes the default worth of an {@link ItemStack}
     *
     * @param stack The {@link ItemStack} to remove the default worth of
     * @throws NoWorthException if the {@link ItemStack} involved doesn't have a worth
     */
    public void removeDefaultWorth(ItemStack stack) throws NoWorthException {
        if (main.getConfig().contains("defined." + stack.getType() + ".default"))
            main.getConfig().set("defined." + stack.getType() + ".default", null);
        else throw new NoWorthException();
    }

    /**
     * Removes the worth of an {@link ItemStack}({@link Material} and data value)
     *
     * @param stack The {@link ItemStack} to remove the worth of
     * @throws NoWorthException if the {@link ItemStack} involved doesn't have a worth
     */
    public void removeWorth(ItemStack stack) throws NoWorthException {
        if (main.getConfig().contains("defined." + stack.getType() + "." + stack.getDurability()))
            main.getConfig().set("defined." + stack.getType() + "." + stack.getDurability(), null);
        else throw new NoWorthException();
    }

    /**
     * Reloads the config and clears the cache
     */
    public void reload() {
        main.reloadConfig();
        initializeCache();
    }

    /**
     * Saves the config and clears the cache
     */
    void cleanup() {
        main.saveConfig();
        itemWorth.invalidateAll();
    }

    /**
     * A {@link CacheLoader} for the worth of an {@link ItemStack}
     *
     * @author Wesley Smith
     */
    private class WorthCacheLoader extends CacheLoader<ItemStack, Double> {

        /**
         * The "defined" path {@link ConfigurationSection}
         */
        private final ConfigurationSection DEFINED_SECTION = main.getConfig().getConfigurationSection("defined");

        /**
         * @inheritDoc
         */
        @Override
        public Double load(ItemStack key) throws Exception {
            final OptionalDouble possibleWorth = getConfigWorth(key); //check for a defined worth
            if (possibleWorth.isPresent()) return possibleWorth.getAsDouble();

            final Optional<Recipe> possibleRecipe = Bukkit.getRecipesFor(key).stream()
                    .filter(recipe -> recipe instanceof ShapelessRecipe || recipe instanceof ShapedRecipe)
                    .sorted((recipe1, recipe2) -> -Integer.compare(recipe1.getResult().getAmount(), recipe2.getResult().getAmount()))
                    .findFirst();
            if (possibleRecipe.isPresent()) {
                final Recipe recipe = possibleRecipe.get();

                Collection<ItemStack> items = Collections.emptyList();
                if (recipe instanceof ShapedRecipe) items = ((ShapedRecipe) recipe).getIngredientMap().values();
                else if (recipe instanceof ShapelessRecipe) items = ((ShapelessRecipe) recipe).getIngredientList();

                double worth = 0;
                for (ItemStack item : items)
                    if (item != null && item.getType() != Material.AIR) {
                        if (item.getDurability() == 32767) //if the recipe item can be of any type
                            item.setDurability(findLowestWorthOfDurability(item).orElse((short) 32767));
                        worth += getWorth(item);
                    }
                return worth / recipe.getResult().getAmount();
            } else throw new NoWorthException();
        }

        /**
         * Gets the worth of an item from the config if it exists
         *
         * @param stack The {@link ItemStack} to find the worth of
         * @return A possible value
         */
        private OptionalDouble getConfigWorth(ItemStack stack) {
            final ConfigurationSection typeSection = DEFINED_SECTION.getConfigurationSection(stack.getType().toString());
            if (typeSection == null) return OptionalDouble.empty();
            else if (typeSection.contains(Short.toString(stack.getDurability())))
                return OptionalDouble.of(typeSection.getDouble(Short.toString(stack.getDurability())));
            else if (typeSection.contains("default")) return OptionalDouble.of(typeSection.getDouble("default"));
            else return OptionalDouble.empty();
        }

        /**
         * Finds the lowest worth value for the item type's durability
         *
         * @param stack The {@link ItemStack} to check from
         * @return A possible value
         */
        private Optional<Short> findLowestWorthOfDurability(ItemStack stack) {
            final ConfigurationSection typeSection = DEFINED_SECTION.getConfigurationSection(stack.getType().toString());
            if (typeSection == null) return Optional.empty();
            else return typeSection.getKeys(false)
                    .stream()
                    .sorted((check1, check2) -> -Integer.compare(typeSection.getInt(check1), typeSection.getInt(check2)))
                    .map(Short::valueOf)
                    .findFirst();
        }

    }

    /**
     * Represents when an item doesn't have a defined worth
     *
     * @author Wesley Smith
     */
    public static class NoWorthException extends Exception {

        /**
         * Creates an NoWorthException with the default message
         */
        public NoWorthException() {
            super("Item does not have a recipe or defined worth!");
        }

    }

}
