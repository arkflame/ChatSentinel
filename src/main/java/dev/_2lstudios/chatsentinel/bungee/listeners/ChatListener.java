package dev._2lstudios.chatsentinel.bungee.listeners;

import java.util.regex.Pattern;

import dev._2lstudios.chatsentinel.bungee.modules.BungeeModuleManager;
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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ChatListener implements Listener {
	private Plugin plugin;
	private BungeeModuleManager moduleManager;
	private ChatPlayerManager chatPlayerManager;

	public ChatListener(Plugin plugin, BungeeModuleManager moduleManager,
			ChatPlayerManager chatPlayerManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.chatPlayerManager = chatPlayerManager;
	}

	private void processModule(ProxyServer server, ProxiedPlayer player, ChatPlayer chatPlayer,
			MessagesModule messagesModule, Module module, ChatEvent event, String playerName, String message,
			String originalMessage, String lang, boolean isCommand, boolean isNormalCommand) {
		if (!player.hasPermission("chatsentinel.bypass." + module.getName())
				&& (!isCommand || module instanceof CooldownModule || module instanceof SyntaxModule
						|| isNormalCommand)
				&& module.meetsCondition(chatPlayer, message)) {
			int warns = chatPlayer.addWarn(module);
			int maxWarns = module.getMaxWarns();
			String[][] placeholders = {
					{ "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%", "%server%" },
					{ playerName, originalMessage, String.valueOf(warns),
							String.valueOf(maxWarns), String.valueOf(0), player.getServer().getInfo().getName() } };

			if (module instanceof SyntaxModule) {
				event.setCancelled(true);
			} else if (module instanceof BlacklistModule) {
				BlacklistModule blacklistModule = (BlacklistModule) module;

				if (blacklistModule.isHideWords()) {
					event.setMessage(
							blacklistModule.getPattern().matcher(event.getMessage()).replaceAll("***"));
				} else
					event.setCancelled(true);
			} else if (module instanceof CapsModule) {
				CapsModule capsModule = (CapsModule) module;

				if (capsModule.isReplace()) {
					event.setMessage(event.getMessage().toLowerCase());
				} else {
					event.setCancelled(true);
				}
			} else if (module instanceof CooldownModule) {
				placeholders[1][4] = String.valueOf(
						((CooldownModule) module).getRemainingTime(chatPlayer, event.getMessage()));

				event.setCancelled(true);
			} else if (module instanceof FloodModule) {
				FloodModule floodModule = (FloodModule) module;

				if (floodModule.isReplace()) {
					String replacedString = floodModule.replace(event.getMessage());

					if (!replacedString.isEmpty()) {
						event.setMessage(replacedString);
					} else {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}

			CommandSender console = server.getConsole();
			String notificationMessage = module.getWarnNotification(placeholders);
			String warnMessage = messagesModule.getWarnMessage(placeholders, lang,
					module.getName());

			if (warnMessage != null && !warnMessage.isEmpty()) {
				player.sendMessage(TextComponent.fromLegacyText(warnMessage));
			}

			if (notificationMessage != null && !notificationMessage.isEmpty()) {
				for (ProxiedPlayer player1 : server.getPlayers()) {
					if (player1.hasPermission("chatsentinel.notify")) {
						player1.sendMessage(TextComponent.fromLegacyText(notificationMessage));
					}
				}

				console.sendMessage(TextComponent.fromLegacyText(notificationMessage));
			}

			if (warns >= maxWarns && maxWarns > 0) {
				PluginManager pluginManager = server.getPluginManager();

				for (String command : module.getCommands(placeholders)) {
					pluginManager.dispatchCommand(console, command);
				}

				chatPlayer.clearWarns();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChatEvent(ChatEvent event) {
		if (!event.isCancelled()) {
			Connection sender = event.getSender();

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				if (!player.hasPermission("chatsentinel.bypass")) {
					ProxyServer server = plugin.getProxy();
					GeneralModule generalModule = moduleManager.getGeneralModule();
					WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
					ChatPlayer chatPlayer = chatPlayerManager.getPlayerOrCreate(player);
					String originalMessage = event.getMessage();
					boolean isCommand = event.isCommand();
					boolean isNormalCommand = generalModule.isCommand(originalMessage);
					String message = originalMessage;
					MessagesModule messagesModule = moduleManager.getMessagesModule();
					String playerName = player.getName();
					String lang = chatPlayer.getLocale();

					processModule(server, player, chatPlayer, messagesModule, moduleManager.getSyntaxModule(), event,
							playerName, message, originalMessage, lang, isCommand, isNormalCommand);
					processModule(server, player, chatPlayer, messagesModule, moduleManager.getCapsModule(), event,
							playerName,
							message, originalMessage, lang, isCommand, isNormalCommand);
					processModule(server, player, chatPlayer, messagesModule, moduleManager.getCooldownModule(), event,
							playerName, message, originalMessage, lang, isCommand, isNormalCommand);
					processModule(server, player, chatPlayer, messagesModule, moduleManager.getFloodModule(), event,
							playerName,
							message, originalMessage, lang, isCommand, isNormalCommand);

					if (isCommand && isNormalCommand && message.contains(" ")) {
						message = message.substring(message.indexOf(" "));
					}

					if (generalModule.isSanitizeEnabled()) {
						message = generalModule.sanitize(message);
					}

					if (generalModule.isSanitizeNames()) {
						message = generalModule.sanitizeNames(message);
					}

					if (whitelistModule.isEnabled()) {
						Pattern whitelistPattern = whitelistModule.getPattern();

						message = whitelistPattern.matcher(message)
								.replaceAll("");
					}

					message = message.trim();

					processModule(server, player, chatPlayer, messagesModule, moduleManager.getBlacklistModule(), event,
							playerName, message, originalMessage, lang, isCommand, isNormalCommand);

					if (!event.isCancelled()) {
						String newMessage = generalModule.isFilterOther() ? generalModule.sanitize(event.getMessage()) : event.getMessage();
				
						if (!newMessage.isEmpty()) {
							long currentMillis = System.currentTimeMillis();
		
							chatPlayer.addLastMessage(message, currentMillis);
							moduleManager.getCooldownModule().setLastMessage(message, currentMillis);
							event.setMessage(newMessage);
						} else {
							event.setCancelled(true);
							player.sendMessage(TextComponent.fromLegacyText(messagesModule.getFiltered(lang)));
						}
					}
				}
			}
		}
	}
}
