package twolovers.chatsentinel.shared.utils;

import java.util.Locale;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class VersionUtil {
	private static boolean oneDotNine = false;

	public static void start(final String version) {
		oneDotNine = !version.contains("1.8") && !version.contains("1.7");
	}

	public static boolean isOneDotNine() {
		return oneDotNine;
	}

	public static String getLocale(final Player player) {
		String locale;

		try {
			player.getClass().getMethod("getLocale");
			locale = player.getLocale();
		} catch (final NoSuchMethodException exception) {
			try {
				player.spigot().getClass().getMethod("getLocale");
				locale = player.getLocale();
			} catch (final Exception exception1) {
				locale = "en";
			}
		}

		if (locale != null && locale.length() > 1) {
			return locale.substring(0, 2);
		} else {
			return "en";
		}
	}

	public static String getLocale(final ProxiedPlayer player) {
		final Locale locale = player.getLocale();

		if (locale != null) {
			final String localeString = locale.toString();

			if (localeString.length() > 1) {
				return localeString.substring(0, 2);
			}
		}

		return "en";
	}
}