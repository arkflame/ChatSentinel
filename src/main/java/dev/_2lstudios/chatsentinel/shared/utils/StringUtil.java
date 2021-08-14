package dev._2lstudios.chatsentinel.shared.utils;

import java.text.Normalizer;

public class StringUtil {
    public static String sanitize(final String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "");
	}

    public static String removeAccents(String message) {
		/*
		 * Removes accents Credit: https://stackoverflow.com/users/636009/david-conrad
		 */
		final char[] out = new char[message.length()];

		message = Normalizer.normalize(message, Normalizer.Form.NFD);

		for (int j = 0, i = 0, n = message.length(); i < n; ++i) {
			final char c = message.charAt(i);

			if (c <= '\u007F') {
				out[j++] = c;
			}
		}

		return new String(out).replace("(punto)", ".").replace("(dot)", ".").trim();
	}
}
