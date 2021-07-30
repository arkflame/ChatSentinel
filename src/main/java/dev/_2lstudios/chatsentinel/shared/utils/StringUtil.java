package dev._2lstudios.chatsentinel.shared.utils;

public class StringUtil {
    final public static String sanitize(final String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "");
	}
}
