package net.wesjd.recursiveitems.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsing utilities for parsing people
 * @author Wesley Smith
 */
public class ParsingUtils {

    //TODO - Convert to using parse format of "<material[:dataValue]>"

    /**
     * The stack regex pattern of "<(\w+)|(\d{1,2})>", used in {@link ParsingUtils#getStackFromArg(String)}
     */
    private static final Pattern STACK_PATTERN = Pattern.compile("<(\\w+)|(\\d{1,2})>");

    /**
     * Creates an {@link ItemStack} from the command argument supplied
     * @param arg The command argument, should be in format of "<material[:dataValue]>"
     * @return The {@link ItemStack} created from the command argument
     * @throws Exception if unable to parse material
     * @throws Exception if argument doesn't match the format
     */
    public static ItemStack getStackFromArg(String arg) throws Exception {
        final String[] parts = arg.split(":");
        if(parts.length == 0 || parts.length <= 2) throw new ArgumentParseException();
        final String argument = parts[0] + ":" + (parts.length > 1 ? parts[1] : 0);

        final Matcher matcher = STACK_PATTERN.matcher(argument);
        if(matcher.matches()) {
            final Material material;
            try {
                material = Material.valueOf(matcher.group(1).toUpperCase());
            } catch (Exception ex) {
                throw new Exception("Unable to parse material of \"" + matcher.group(1) + "\".");
            }
            return new ItemStack(material, 1, Short.parseShort(matcher.group(2)));
        } else throw new ArgumentParseException();
    }

    private static class ArgumentParseException extends Exception {

        public ArgumentParseException() {
            super("Provided argument does not follow stack parsing format of <material[:dataValue]>.");
        }

    }

}
