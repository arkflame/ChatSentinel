package dev._2lstudios.chatsentinel.bukkit.listeners;

import java.util.Collection;
import java.util.regex.Pattern;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import dev._2lstudios.chatsentinel.bukkit.ChatSentinel;
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
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;

public class AsyncPlayerChatListener implements Listener {
	private ChatSentinel chatSentinel;
	private BukkitModuleManager moduleManager;
	private ChatPlayerManager chatPlayerManager;

	public AsyncPlayerChatListener(ChatSentinel chatSentinel, BukkitModuleManager moduleManager,
			ChatPlayerManager chatPlayerManager) {
		this.chatSentinel = chatSentinel;
		this.moduleManager = moduleManager;
		this.chatPlayerManager = chatPlayerManager;
	}

	private void processModule(Server server, Player player, ChatPlayer chatPlayer, MessagesModule messagesModule, Module module, AsyncPlayerChatEvent event, String playerName, String message, String originalMessage, String lang) {
		if (!player.hasPermission("chatsentinel.bypass." + module.getName())
				&& module.meetsCondition(chatPlayer, message)) {
			Collection<Player> recipients = event.getRecipients();
			int warns = chatPlayer.addWarn(module), maxWarns = module.getMaxWarns();
			String[][] placeholders = {
					{ "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%" }, { playerName, originalMessage,
							String.valueOf(warns), String.valueOf(module.getMaxWarns()), String.valueOf(0) } };

			if (module instanceof BlacklistModule) {
				BlacklistModule blacklistModule = (BlacklistModule) module;

				if (blacklistModule.isFakeMessage()) {
					recipients.removeIf(player1 -> player1 != player);
				} else if (blacklistModule.isHideWords()) {
					event.setMessage(blacklistModule.getPattern().matcher(event.getMessage()).replaceAll("***"));
				} else {
					event.setCancelled(true);
				}
			} else if (module instanceof CapsModule) {
				CapsModule capsModule = (CapsModule) module;

				if (capsModule.isReplace()) {
					event.setMessage(event.getMessage().toLowerCase());
				} else {
					event.setCancelled(true);
				}
			} else if (module instanceof CooldownModule) {
				placeholders[1][4] = String
						.valueOf(((CooldownModule) module).getRemainingTime(chatPlayer, message));

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
				server.getScheduler().runTask(chatSentinel, () -> {
					for (String command : module.getCommands(placeholders)) {
						server.dispatchCommand(server.getConsoleSender(), command);
					}
				});

				chatPlayer.clearWarns();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("chatsentinel.bypass")) {
			ChatPlayer chatPlayer = chatPlayerManager.getPlayerOrCreate(player);
			GeneralModule generalModule = moduleManager.getGeneralModule();
			String originalMessage = event.getMessage();
			MessagesModule messagesModule = moduleManager.getMessagesModule();
			WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
			Server server = chatSentinel.getServer();
			String playerName = player.getName();
			String lang = chatPlayer.getLocale();
			String message = originalMessage;

			processModule(server, player, chatPlayer, messagesModule, moduleManager.getCapsModule(), event, playerName, message, originalMessage, lang);
			if (event.isCancelled()) return;
			processModule(server, player, chatPlayer, messagesModule, moduleManager.getCooldownModule(), event, playerName, message, originalMessage, lang);
			if (event.isCancelled()) return;
			processModule(server, player, chatPlayer, messagesModule, moduleManager.getFloodModule(), event, playerName, message, originalMessage, lang);
			if (event.isCancelled()) return;

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
			processModule(server, player, chatPlayer, messagesModule, moduleManager.getBlacklistModule(), event, playerName, message, originalMessage, lang);

			if (!event.isCancelled()) {
				String newMessage = generalModule.isFilterOther() ? generalModule.sanitize(event.getMessage()) : event.getMessage();
				
				if (!newMessage.isEmpty()) {
					long currentMillis = System.currentTimeMillis();

					chatPlayer.addLastMessage(message, currentMillis);
					moduleManager.getCooldownModule().setLastMessage(message, currentMillis);
					event.setMessage(newMessage);
				} else {
					event.setCancelled(true);
					player.sendMessage(messagesModule.getFiltered(lang));
				}
			}
		}
	}
}
