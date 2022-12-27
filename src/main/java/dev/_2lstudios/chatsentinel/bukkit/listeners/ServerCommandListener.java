package dev._2lstudios.chatsentinel.bukkit.listeners;

import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import dev._2lstudios.chatsentinel.bukkit.modules.BukkitModuleManager;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.modules.BlacklistModule;
import dev._2lstudios.chatsentinel.shared.modules.CapsModule;
import dev._2lstudios.chatsentinel.shared.modules.CooldownModule;
import dev._2lstudios.chatsentinel.shared.modules.FloodModule;
import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;
import dev._2lstudios.chatsentinel.shared.modules.MessagesModule;
import dev._2lstudios.chatsentinel.shared.modules.SyntaxModule;
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;

public class ServerCommandListener implements Listener {
	private Plugin plugin;
	private BukkitModuleManager moduleManager;
	private ChatPlayerManager chatPlayerManager;

	public ServerCommandListener(Plugin plugin, BukkitModuleManager moduleManager,
			ChatPlayerManager chatPlayerManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.chatPlayerManager = chatPlayerManager;
	}

	private void processModule(Server server, Player player, ChatPlayer chatPlayer, MessagesModule messagesModule,
			Module module, PlayerCommandPreprocessEvent event, String playerName, String message,
			String originalMessage, String lang, boolean isNormalCommand) {
		{
			if (!player.hasPermission("chatsentinel.bypass." + module.getName())
					&& (module instanceof CooldownModule || module instanceof SyntaxModule || isNormalCommand)
					&& module.meetsCondition(chatPlayer, message)) {
				int warns = chatPlayer.addWarn(module), maxWarns = module.getMaxWarns();
				String[][] placeholders = {
						{ "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%" },
						{ playerName, originalMessage, String.valueOf(warns), String.valueOf(module.getMaxWarns()),
								String.valueOf(0) } };

				if (module instanceof SyntaxModule) {
					event.setCancelled(true);
				} else if (module instanceof BlacklistModule) {
					BlacklistModule blacklistModule = (BlacklistModule) module;

					if (blacklistModule.isHideWords()) {
						event.setMessage(blacklistModule.getPattern().matcher(event.getMessage()).replaceAll("***"));
					} else
						event.setCancelled(true);
				} else if (module instanceof CapsModule) {
					CapsModule capsModule = (CapsModule) module;

					if (capsModule.isReplace())
						event.setMessage(event.getMessage().toLowerCase());
					else
						event.setCancelled(true);
				} else if (module instanceof CooldownModule) {
					placeholders[1][4] = String
							.valueOf(((CooldownModule) module).getRemainingTime(chatPlayer, message));

					event.setCancelled(true);
				} else if (module instanceof FloodModule) {
					FloodModule floodModule = (FloodModule) module;

					if (floodModule.isReplace()) {
						String replacedString = floodModule.replace(event.getMessage());

						if (!replacedString.isEmpty())
							event.setMessage(replacedString);
						else
							event.setCancelled(true);
					} else
						event.setCancelled(true);
				} else
					event.setCancelled(true);

				String notificationMessage = module.getWarnNotification(placeholders);
				String warnMessage = messagesModule.getWarnMessage(placeholders, lang, module.getName());

				if (warnMessage != null && !warnMessage.isEmpty())
					player.sendMessage(warnMessage);

				if (notificationMessage != null && !notificationMessage.isEmpty()) {
					for (Player player1 : server.getOnlinePlayers()) {
						if (player1.hasPermission("chatsentinel.notify"))
							player1.sendMessage(notificationMessage);
					}

					server.getConsoleSender().sendMessage(notificationMessage);
				}

				if (warns >= maxWarns && maxWarns > 0) {
					server.getScheduler().runTask(plugin, () -> {
						for (String command : module.getCommands(placeholders)) {
							server.dispatchCommand(server.getConsoleSender(), command);
						}
					});

					chatPlayer.clearWarns();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onServerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("chatsentinel.bypass")) {
			Server server = plugin.getServer();
			UUID uuid = player.getUniqueId();
			GeneralModule generalModule = moduleManager.getGeneralModule();
			WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
			ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);
			String originalMessage = event.getMessage();
			boolean isNormalCommand = generalModule.isCommand(originalMessage);
			String message = originalMessage;

			if (isNormalCommand && originalMessage.contains(" ")) {
				message = message.substring(message.indexOf(" "));
			}

			if (generalModule.isSanitizeEnabled()) {
				message = generalModule.sanitize(message);
			}

			if (generalModule.isSanitizeNames()) {
				message = generalModule.sanitizeNames(server, message);
			}

			if (whitelistModule.isEnabled()) {
				Pattern whitelistPattern = whitelistModule.getPattern();

				message = whitelistPattern.matcher(message)
						.replaceAll("");
			}

			message = message.trim();

			MessagesModule messagesModule = moduleManager.getMessagesModule();
			String playerName = player.getName();
			String lang = VersionUtil.getLocale(player);

			processModule(server, player, chatPlayer, messagesModule, moduleManager.getCapsModule(), event, playerName,
					message, originalMessage, lang, isNormalCommand);
			processModule(server, player, chatPlayer, messagesModule, moduleManager.getCooldownModule(), event,
					playerName, message, originalMessage, lang, isNormalCommand);
			processModule(server, player, chatPlayer, messagesModule, moduleManager.getFloodModule(), event, playerName,
					message, originalMessage, lang, isNormalCommand);
			processModule(server, player, chatPlayer, messagesModule, moduleManager.getBlacklistModule(), event,
					playerName, message, originalMessage, lang, isNormalCommand);
			processModule(server, player, chatPlayer, messagesModule, moduleManager.getSyntaxModule(), event,
					playerName, message, originalMessage, lang, isNormalCommand);

			if (!event.isCancelled()) {
				chatPlayer.addLastMessage(message, System.currentTimeMillis());
			}
		}
	}
}
