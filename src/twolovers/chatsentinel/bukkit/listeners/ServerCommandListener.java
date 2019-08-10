package twolovers.chatsentinel.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import twolovers.chatsentinel.bukkit.variables.PatternVariables;
import twolovers.chatsentinel.bukkit.variables.PluginVariables;
import twolovers.chatsentinel.bukkit.variables.SwearingVariables;
import twolovers.chatsentinel.bukkit.variables.SyntaxVariables;

import java.util.regex.Pattern;

public class ServerCommandListener implements Listener {
	final private Plugin plugin;
	final private PluginVariables pluginVariables;
	private final PatternVariables patternVariables;
	private final SwearingVariables swearingVariables;
	private final SyntaxVariables syntaxVariables;

	public ServerCommandListener(Plugin plugin, PluginVariables pluginVariables) {
		this.plugin = plugin;
		this.pluginVariables = pluginVariables;
		patternVariables = pluginVariables.getPatternVariables();
		swearingVariables = pluginVariables.getSwearingVariables();
		syntaxVariables = pluginVariables.getSyntaxVariables();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onServerCommand(final PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			final Player player = event.getPlayer();

			if (!player.hasPermission("chatsentinel.bypass")) {
				final Pattern whitelistPattern = patternVariables.getWhitelistPattern();
				String message = formatMessage(event.getMessage());

				if (whitelistPattern != null) {
					message = whitelistPattern.matcher(message).replaceAll(" ");
					message = patternVariables.getNamesPattern().matcher(message).replaceAll(" ");
					message = message.trim();
				}

				if (swearingVariables.isSwearingEnabled() && !message.isEmpty() && patternVariables.startsWithCommand(event.getMessage()) && patternVariables.getBlacklistPattern().matcher(message).find()) {
					event.setCancelled(true);
					pluginVariables.addWarn(player);

					final int warns = pluginVariables.getWarns(player);
					final String playerName = player.getName();
					final String swearingWarnMessage = swearingVariables.getSwearingWarnMessage().replace("%warns%", String.valueOf(warns));
					final String swearingPunishCommand = swearingVariables.getSwearingPunishCommand().replace("%player%", playerName).replace("%message%", message);
					final String swearingWarnNotification = swearingVariables.getSwearingWarnNotification().replace("%player%", playerName).replace("%message%", message);

					player.sendMessage(swearingWarnMessage);

					if (warns == swearingVariables.getMaxWarnings() && !swearingPunishCommand.equals("")) {
						pluginVariables.removeWarns(player);
						Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), swearingPunishCommand));
					}

					if (!swearingWarnNotification.equals("")) {
						for (final Player player1 : Bukkit.getOnlinePlayers()) {
							if (player1.hasPermission("chatsentinel.notify"))
								player1.sendMessage(swearingWarnNotification);
						}

						Bukkit.getConsoleSender().sendMessage(swearingWarnNotification);
					}
				} else if (syntaxVariables.isSyntaxEnabled() && !syntaxVariables.isWhitelisted(event.getMessage()) && syntaxVariables.getSyntaxPattern().matcher(event.getMessage()).find()) {
					player.sendMessage(syntaxVariables.getSyntaxWarnMessage());
					event.setCancelled(true);
				}
			}
		}
	}

	private String formatMessage(String string) {
		return string.replace("á", "a")
				.replace("é", "e")
				.replace("í", "i")
				.replace("ó", "o")
				.replace("ú", "u")
				.replace("(punto)", ".")
				.replace("(dot)", ".")
				.replaceAll("^/([a-z])*", "");
	}
}
