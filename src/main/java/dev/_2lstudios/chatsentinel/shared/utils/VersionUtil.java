package dev._2lstudios.chatsentinel.shared.utils;

import java.lang.reflect.Method;
import java.util.Locale;

import org.bukkit.entity.Entity.Spigot;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class VersionUtil {
	private static boolean oneDotNine = false;

	public static void start(String version) {
		oneDotNine = !version.contains("1.8") && !version.contains("1.7");
	}

	public static boolean isOneDotNine() {
		return oneDotNine;
	}

	private static String trimLocale(String locale) {
		return locale.substring(0, 2);
	}

	public static String getLocale(Player player) {
		String locale;

		try {
			Method method = player.getClass().getMethod("getLocale");
			locale = (String) method.invoke(player);
		} catch (Exception exception) {
			try {
				Spigot playerSpigot = player.spigot();
				Method method = playerSpigot.getClass().getMethod("getLocale");
				locale = (String) method.invoke(playerSpigot);
			} catch (Exception exception1) {
				locale = "en";
			}
		}

		if (locale != null && locale.length() > 1) {
			return trimLocale(locale);
		} else {
			return "en";
		}
	}

	public static String getLocale(ProxiedPlayer player) {
		Locale locale = player.getLocale();

		if (locale != null) {
			String localeString = locale.toString();

			if (localeString.length() > 1) {
				return trimLocale(localeString);
			}
		}

		return "en";
	}
}