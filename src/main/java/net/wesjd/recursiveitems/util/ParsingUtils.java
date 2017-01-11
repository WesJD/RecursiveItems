package net.wesjd.recursiveitems.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsing utilities for parsing people
 *
 * @author Wesley Smith
 */
public class ParsingUtils {

    /**
     * The stack regex pattern of "(\w+)(?::(\d{1,2}))?", used in {@link ParsingUtils#getStackFromArg(String)}
     */
    private static final Pattern STACK_PATTERN = Pattern.compile("(\\w+)(?::(\\d{1,2}))?");

    /**
     * Creates an {@link ItemStack} from the command argument supplied
     *
     * @param arg The command argument, should be in format of "<material[:dataValue]>"
     * @return The {@link ItemStack} created from the command argument
     * @throws ArgumentParseException if unable to parse material
     * @throws ArgumentParseException if argument doesn't match the format
     */
    public static ItemStack getStackFromArg(String arg) throws Exception {
        final Matcher matcher = STACK_PATTERN.matcher(arg);
        if(matcher.matches()) {
            final Material material;
            try {
                material = Material.valueOf(matcher.group(1).toUpperCase());
            } catch (Exception ex) {
                throw new ArgumentParseException("Unable to parse material of \"" + matcher.group(1) + "\".");
            }
            return new ItemStack(material, 1, matcher.group(2) != null ? Short.parseShort(matcher.group(2)) : 0);
        } else throw new ArgumentParseException();
    }

    /**
     * Represents when the argument is unable to be parsed, used in {@link ParsingUtils#getStackFromArg(String)}
     *
     * @author Wesley Smith
     */
    private static class ArgumentParseException extends Exception {

        /**
         * Creates an ArgumentParseException with the default message
         */
        public ArgumentParseException() {
            super("Provided argument does not follow stack parsing format of <material[:dataValue]>.");
        }

        /**
         * Creates and ArgumentParseException with a defined message
         * @param msg The defined message
         */
        public ArgumentParseException(String msg) {
            super(msg);
        }

    }

}
