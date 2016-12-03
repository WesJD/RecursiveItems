package net.wesjd.recursiveitems;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
     * @throws ExecutionException if any base items involved in the crafting of this item do not have a defined price
     * @throws ExecutionException if the {@link LoadingCache} throws it
     */
    public double getWorth(ItemStack stack) throws ExecutionException {
        return itemWorth.get(stack) * stack.getAmount();
    }

    /**
     * Recursively searches for the worth of this {@link ItemStack[]} based on it's ingredients
     *
     * @param stacks The {@link ItemStack[]} to find the worth of
     * @return The item's worth or 0 if none of the items have a defined price
     */
    public double getWorth(ItemStack... stacks) {
        return Arrays.stream(stacks).filter(Objects::nonNull).mapToDouble(s -> {

            try {
                double w = getWorth(s);
                System.out.println("FOUND WORTH: "+w);
                return w;
            } catch (ExecutionException e) {
                System.out.println("NO PRICE DEFINED");
                return 0; //no defined price
            }
        }).sum();
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
     * Removes the worth of an {@link ItemStack}({@link Material} and data value)
     *
     * @param stack The {@link ItemStack} to remove the worth of
     * @throws Exception if the {@link ItemStack} involved doesn't have a worth
     */
    public void removeWorth(ItemStack stack) throws Exception {
        if (main.getConfig().contains("defined." + stack.getType()))
            main.getConfig().set("defined." + stack.getType(), null);
        else throw new Exception("No explicit worth set for ItemStack");
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
         * Returns an {@link OptionalDouble} of the config's set worth value
         */
        private final Function<ItemStack, OptionalDouble> configWorth = (stack) -> {
            if (main.getConfig().contains("defined." + stack.getType() + "." + stack.getDurability()))
                return OptionalDouble.of(main.getConfig().getDouble("defined." + stack.getType() + "." + stack.getDurability()));
            return OptionalDouble.empty();
        };

        /**
         * @inheritDoc
         */
        @Override
        public Double load(ItemStack key) throws Exception {
            final OptionalDouble possibleWorth = configWorth.apply(key); //check for a defined worth
            if (possibleWorth.isPresent()) return possibleWorth.getAsDouble();

            final Optional<Recipe> possibleRecipe = Bukkit.getRecipesFor(key).stream()
                    .filter(recipe -> recipe instanceof ShapelessRecipe || recipe instanceof ShapedRecipe)
                    .sorted((recipe1, recipe2) -> -Integer.compare(recipe1.getResult().getAmount(), recipe2.getResult().getAmount()))
                    .findFirst();
            if (possibleRecipe.isPresent()) { //
                final Recipe recipe = possibleRecipe.get();

                Collection<ItemStack> items = Collections.emptyList();
                if (recipe instanceof ShapedRecipe) items = ((ShapedRecipe) recipe).getIngredientMap().values();
                else if (recipe instanceof ShapelessRecipe) items = ((ShapelessRecipe) recipe).getIngredientList();

                final Exception[] toThrow = new Exception[1];
                final double worth = items.stream()
                        .mapToDouble(item -> configWorth.apply(item).orElseGet(() -> {
                            try {
                                return load(item);
                            } catch (Exception ex) {
                                toThrow[0] = ex;
                                return -Double.MAX_VALUE;
                            }
                        }))
                        .sum();
                if (toThrow[0] != null) throw toThrow[0];
                return worth / recipe.getResult().getAmount();
            } else throw new Exception("Item does not have a recipe or defined worth.");
        }

    }

}
