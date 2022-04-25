package dev._2lstudios.chatsentinel.shared.modules;

import java.text.Normalizer;
import java.util.Collection;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GeneralModule {
	private boolean sanitize;
	private boolean sanitizeNames;
	private Collection<String> commands;

	public void loadData(final boolean sanitize, final boolean sanitizeNames, final Collection<String> commands) {
		this.sanitize = sanitize;
		this.sanitizeNames = sanitizeNames;
		this.commands = commands;
	}

    public boolean isSanitizeEnabled() {
        return sanitize;
    }

	/*
	* Removes non latin words Credit: https://stackoverflow.com/users/636009/david-conrad
	*/
    public String sanitize(String message) {
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

	public boolean isSanitizeNames() {
        return sanitizeNames;
    }

	public String sanitizeNames(ProxyServer server, String message) {
		for (ProxiedPlayer player : server.getPlayers()) {
			message = message.replace(player.getName(), "");
		}

		return message;
	}

	public String sanitizeNames(Server server, String message) {
		for (Player player : server.getOnlinePlayers()) {
			message = message.replace(player.getName(), "");
		}

		return message;
	}

	public boolean isCommand(String message) {
		message = message.toLowerCase();

		for (final String command : commands) {
			if (message.startsWith(command + ' '))
				return true;
		}

		return false;
	}
}
