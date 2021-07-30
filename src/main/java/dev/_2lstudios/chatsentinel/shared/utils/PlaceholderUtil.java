package dev._2lstudios.chatsentinel.shared.utils;

public class PlaceholderUtil {
    public static String replacePlaceholders(String string, final String[][] placeholders) {
        string = string.replace('\u0026', '\u00a7');

        if (placeholders != null && placeholders.length > 0) {
            for (int i = 0; i < placeholders[0].length; i++) {
                final String id = placeholders[0][i], value = placeholders[1][i];

                string = string.replace(id, value);
            }
        }

        return string;
    }
}