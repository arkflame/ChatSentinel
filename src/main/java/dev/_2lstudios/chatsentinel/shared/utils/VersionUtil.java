package dev._2lstudios.chatsentinel.shared.utils;

import java.lang.invoke.MethodHandle;
import java.util.Locale;

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
		String locale = null;

		if (player != null && player.isOnline()) {
			MethodHandle getLocaleMethod = ReflectionUtil.getLocalePlayerMethod();

			try {
				if (getLocaleMethod != null) {
					locale = getLocaleMethod.invoke(player).toString();
				} else {
					getLocaleMethod = ReflectionUtil.getLocaleSpigotMethod();
					if (getLocaleMethod != null) {
						locale = getLocaleMethod.invoke(player.spigot()).toString();
					}
				}
			} catch (Throwable t) {
				// The player is invalid, ignore
			}
			

			if (locale != null && locale.length() > 1) {
				locale = locale.substring(0, 2);
			}
		}

		return locale;
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