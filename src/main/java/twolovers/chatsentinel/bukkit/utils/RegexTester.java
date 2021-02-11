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

    public static boolean test(String arguments) {
        String userInputPattern = arguments;
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            return false;
        }
        return true;
    }

    public static List<String> getValidExpressions(String[] expressions) {
        List<String> acceptedExpressions = new ArrayList<>();
        for (String expression : expressions) {
            if (test(expression)) {
                acceptedExpressions.add(expression);
            } else {
                System.out.println("[ChatSentinel] Expression "+expression+" was not accepted by the regex parser");
            }
        }
        return acceptedExpressions;
    }
}
