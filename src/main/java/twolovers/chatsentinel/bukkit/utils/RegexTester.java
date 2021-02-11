package twolovers.chatsentinel.bukkit.utils;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexTester {
    final private Plugin plugin;

    public RegexTester(final Plugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isValid(String arguments) {
        String userInputPattern = arguments;
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            return false;
        }
        return true;
    }

    public static List<String> getValidExpressions(List<String> expressions) {
        expressions.removeIf(s -> {
            if (isValid(s)) {
                return false;
            }
            System.out.println("[ChatSentinel] Expression "+s+" was not accepted by the regex parser");
            return true;
        });
        return expressions;
    }
}
