package dev._2lstudios.chatsentinel.shared.utils;

import java.lang.reflect.Method;
import java.util.Locale;

import org.bukkit.entity.Entity.Spigot;
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

	private static String trimLocale(final String locale) {
		return locale.substring(0, 2);
	}

	public static String getLocale(final Player player) {
		String locale;

		try {
			final Method method = player.getClass().getMethod("getLocale");
			locale = (String) method.invoke(player);
		} catch (final Exception exception) {
			try {
				final Spigot playerSpigot = player.spigot();
				final Method method = playerSpigot.getClass().getMethod("getLocale");
				locale = (String) method.invoke(playerSpigot);
			} catch (final Exception exception1) {
				locale = "en";
			}
		}

		if (locale != null && locale.length() > 1) {
			return trimLocale(locale);
		} else {
			return "en";
		}
	}

	public static String getLocale(final ProxiedPlayer player) {
		final Locale locale = player.getLocale();

		if (locale != null) {
			final String localeString = locale.toString();

			if (localeString.length() > 1) {
				return trimLocale(localeString);
			}
		}

		return "en";
	}

	public static String getLocale(final com.velocitypowered.api.proxy.Player player) {
		final Locale locale = player.getEffectiveLocale();

		if (locale != null) {
			final String localeString = locale.toString();

			if (localeString.length() > 1) {
				return trimLocale(localeString);
			}
		}

		return "en";
	}
}