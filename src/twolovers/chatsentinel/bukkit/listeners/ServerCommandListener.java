package twolovers.chatsentinel.bukkit.listeners;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import twolovers.chatsentinel.bukkit.modules.ModuleManager;
import twolovers.chatsentinel.bukkit.utils.VersionUtil;
import twolovers.chatsentinel.shared.chat.ChatPlayer;
import twolovers.chatsentinel.shared.chat.ChatPlayerManager;
import twolovers.chatsentinel.shared.interfaces.Module;
import twolovers.chatsentinel.shared.modules.BlacklistModule;
import twolovers.chatsentinel.shared.modules.CapsModule;
import twolovers.chatsentinel.shared.modules.CooldownModule;
import twolovers.chatsentinel.shared.modules.FloodModule;
import twolovers.chatsentinel.shared.modules.MessagesModule;
import twolovers.chatsentinel.shared.modules.SyntaxModule;
import twolovers.chatsentinel.shared.modules.WhitelistModule;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

public class ServerCommandListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;
	private final ChatPlayerManager chatPlayerManager;

	public ServerCommandListener(final Plugin plugin, final ModuleManager moduleManager,
			final ChatPlayerManager chatPlayerManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.chatPlayerManager = chatPlayerManager;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onServerCommand(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();

		if (!player.hasPermission("chatsentinel.bypass")) {
			final UUID uuid = player.getUniqueId();
			final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
			final Pattern whitelistPattern = whitelistModule.getPattern();
			final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);
			final String message = event.getMessage();
			String modifiedMessage = formatMessage(message);

			if (whitelistPattern != null) {
				modifiedMessage = whitelistPattern.matcher(modifiedMessage).replaceAll(" ");
				modifiedMessage = modifiedMessage.trim();
			}

			final MessagesModule messagesModule = moduleManager.getMessagesModule();
			final Server server = plugin.getServer();
			final String playerName = player.getName();
			final String lang;

			if (VersionUtil.isOneDotNine()) {
				lang = player.getLocale();
			} else
				lang = player.spigot().getLocale();

			for (final Module module : moduleManager.getModules()) {
				if (!player.hasPermission("chatsentinel.bypass." + module.getName())
						&& (module instanceof CooldownModule || module instanceof SyntaxModule
								|| whitelistModule.startsWithCommand(message))
						&& module.meetsCondition(chatPlayer, modifiedMessage)) {
					final int warns = chatPlayer.addWarn(module), maxWarns = module.getMaxWarns();
					final String[][] placeholders = {
							{ "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%" }, { playerName, message,
									String.valueOf(warns), String.valueOf(module.getMaxWarns()), String.valueOf(0) } };

					if (module instanceof BlacklistModule) {
						final BlacklistModule blacklistModule = (BlacklistModule) module;

						if (blacklistModule.isHideWords()) {
							event.setMessage(blacklistModule.getPattern().matcher(modifiedMessage).replaceAll("***"));
						} else
							event.setCancelled(true);
					} else if (module instanceof CapsModule) {
						final CapsModule capsModule = (CapsModule) module;

						if (capsModule.isReplace())
							event.setMessage(message.toLowerCase());
						else
							event.setCancelled(true);
					} else if (module instanceof CooldownModule) {
						placeholders[1][4] = String
								.valueOf(((CooldownModule) module).getRemainingTime(chatPlayer, message));

						event.setCancelled(true);
					} else if (module instanceof FloodModule) {
						final FloodModule floodModule = (FloodModule) module;

						if (floodModule.isReplace()) {
							final String replacedString = floodModule.replacePattern(message);

							if (!replacedString.isEmpty())
								event.setMessage(replacedString);
							else
								event.setCancelled(true);
						} else
							event.setCancelled(true);
					} else
						event.setCancelled(true);

					final String notificationMessage = module.getWarnNotification(placeholders);
					final String warnMessage = messagesModule.getWarnMessage(placeholders, lang, module.getName());

					if (warnMessage != null && !warnMessage.isEmpty())
						player.sendMessage(warnMessage);

					if (notificationMessage != null && !notificationMessage.isEmpty()) {
						for (final Player player1 : server.getOnlinePlayers()) {
							if (player1.hasPermission("chatsentinel.notify"))
								player1.sendMessage(notificationMessage);
						}

						server.getConsoleSender().sendMessage(notificationMessage);
					}

					if (warns >= maxWarns && maxWarns > 0) {
						server.getScheduler().runTask(plugin, () -> {
							for (final String command : module.getCommands(placeholders)) {
								server.dispatchCommand(server.getConsoleSender(), command);
							}
						});

						chatPlayer.clearWarns();

						if (event.isCancelled())
							break;
					}
				}
			}

			if (!event.isCancelled())
				chatPlayer.addLastMessage(modifiedMessage, System.currentTimeMillis());
		}
	}

	private String formatMessage(final String string) {
		return Normalizer.normalize(string.replace("[(]?punto[)]?", ".").replace("[(]?dot[)]?", "."),
				Normalizer.Form.NFKD);
	}
}
