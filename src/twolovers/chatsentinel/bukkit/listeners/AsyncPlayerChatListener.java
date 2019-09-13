package twolovers.chatsentinel.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import twolovers.chatsentinel.bukkit.variables.*;

import java.util.Collection;
import java.util.regex.Pattern;

public class AsyncPlayerChatListener implements Listener {
	final private Plugin plugin;
	final private PluginVariables pluginVariables;
	private final FloodVariables floodVariables;
	private final PatternVariables patternVariables;
	private final SwearingVariables swearingVariables;
	private final CooldownVariables cooldownVariables;

	public AsyncPlayerChatListener(final Plugin plugin, final PluginVariables pluginVariables) {
		this.plugin = plugin;
		this.pluginVariables = pluginVariables;
		floodVariables = pluginVariables.getFloodVariables();
		patternVariables = pluginVariables.getPatternVariables();
		swearingVariables = pluginVariables.getSwearingVariables();
		cooldownVariables = pluginVariables.getCooldownVariables();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();

		if (!player.hasPermission("chatsentinel.bypass")) {
			final Pattern whitelistPattern = patternVariables.getWhitelistPattern();
			final long currentTimeMillis = System.currentTimeMillis();
			final long throttleTime = currentTimeMillis - pluginVariables.getThrottle(player);
			String message = event.getMessage();
			String modifiedMessage = translateCharacters(message);

			if (whitelistPattern != null) {
				modifiedMessage = whitelistPattern.matcher(modifiedMessage).replaceAll(" ");
				modifiedMessage = patternVariables.getNamesPattern().matcher(modifiedMessage).replaceAll(" ");
				modifiedMessage = modifiedMessage.trim();
			}

			if (swearingVariables.isSwearingEnabled() && !player.hasPermission("chatsentinel.bypass.swearing") && !modifiedMessage.isEmpty() && patternVariables.getBlacklistPattern().matcher(modifiedMessage).find()) {
				pluginVariables.addWarn(player);

				final int warns = pluginVariables.getWarns(player);
				final String playerName = player.getName();
				final String swearingWarnMessage = swearingVariables.getSwearingWarnMessage().replace("%warns%", String.valueOf(warns));
				final String swearingPunishCommand = swearingVariables.getSwearingPunishCommand().replace("%player%", playerName).replace("%message%", modifiedMessage);
				final String swearingWarnNotification = swearingVariables.getSwearingWarnNotification().replace("%player%", playerName).replace("%message%", modifiedMessage);

				if (!swearingWarnMessage.isEmpty())
					player.sendMessage(swearingWarnMessage);

				if (swearingVariables.isFakeMessage()) {
					final Collection<Player> recipients = event.getRecipients();

					recipients.removeIf(player1 -> player1 != player);
				} else if (swearingVariables.isHideWords())
					event.setMessage(message.replaceAll(patternVariables.getBlacklistPattern().toString(), "***"));
				else
					event.setCancelled(true);

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
			} else if (cooldownVariables.isEnabled() && !player.hasPermission("chatsentinel.bypass.cooldown") && throttleTime < cooldownVariables.getThrottleTime()) {
				player.sendMessage(cooldownVariables.getWarnMessage());
				event.setCancelled(true);
			} else if (floodVariables.isEnabled() && !player.hasPermission("chatsentinel.bypass.flood") && throttleTime < floodVariables.getFloodTime() && message.contains(pluginVariables.getLastMessage(player))) {
				player.sendMessage(floodVariables.getFloodWarnMessage());
				event.setCancelled(true);
			} else {
				pluginVariables.setLastMessage(player, message);
				pluginVariables.setThrottle(player, currentTimeMillis);
			}
		}
	}

	private String translateCharacters(final String string) {
		return string.replace("á", "a")
				.replace("é", "e")
				.replace("í", "i")
				.replace("ó", "o")
				.replace("ú", "u")
				.replace("[(]?punto[)]?", ".")
				.replace("[(]?dot[)]?", ".");
	}
}
