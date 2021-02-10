package twolovers.chatsentinel.bukkit.utils;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexTester {
    final private Plugin plugin;

    public RegexTester(final Plugin plugin) {
        this.plugin = plugin;
    }

    public static boolean test(String arguments) {
        String userInputPattern = arguments;
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            return false;
        }
        return true;
    }
}
