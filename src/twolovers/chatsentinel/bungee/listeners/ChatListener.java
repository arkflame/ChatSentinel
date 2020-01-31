package twolovers.chatsentinel.bungee.listeners;

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
import twolovers.chatsentinel.bungee.modules.*;
import twolovers.chatsentinel.shared.chat.ChatPlayer;
import twolovers.chatsentinel.shared.chat.ChatPlayerManager;
import twolovers.chatsentinel.shared.interfaces.Module;
import twolovers.chatsentinel.shared.modules.CapsModule;
import twolovers.chatsentinel.shared.modules.CooldownModule;
import twolovers.chatsentinel.shared.modules.BlacklistModule;
import twolovers.chatsentinel.shared.modules.FloodModule;
import twolovers.chatsentinel.shared.modules.MessagesModule;
import twolovers.chatsentinel.shared.modules.SyntaxModule;
import twolovers.chatsentinel.shared.modules.WhitelistModule;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
	final private Plugin plugin;
	final private ModuleManager moduleManager;
	final private ChatPlayerManager chatPlayerManager;

	public ChatListener(final Plugin plugin, final ModuleManager moduleManager,
			final ChatPlayerManager chatPlayerManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.chatPlayerManager = chatPlayerManager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatEvent(final ChatEvent event) {
		if (!event.isCancelled()) {
			final Connection sender = event.getSender();

			if (sender instanceof ProxiedPlayer) {
				final ProxiedPlayer player = (ProxiedPlayer) sender;

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

					if (!modifiedMessage.isEmpty()) {
						final MessagesModule messagesModule = moduleManager.getMessagesModule();
						final ProxyServer server = plugin.getProxy();
						final String playerName = player.getName(), lang;
						final Locale locale = player.getLocale();

						if (locale != null)
							lang = locale.toLanguageTag();
						else
							lang = null;

						for (final Module module : moduleManager.getModules()) {
							if (!player.hasPermission("chatsentinel.bypass." + module.getName())
									&& (!event.isCommand() || module instanceof CooldownModule
											|| module instanceof FloodModule || module instanceof SyntaxModule
											|| whitelistModule.startsWithCommand(message))
									&& module.meetsCondition(chatPlayer, modifiedMessage)) {
								final int warns = chatPlayer.addWarn(module), maxWarns = module.getMaxWarns();
								final String[][] placeholders = {
										{ "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%" },
										{ playerName, message, String.valueOf(warns),
												String.valueOf(module.getMaxWarns()), String.valueOf(0) } };

								if (module instanceof BlacklistModule) {
									final BlacklistModule blacklistModule = (BlacklistModule) module;

									if (blacklistModule.isHideWords()) {
										event.setMessage(blacklistModule.getPattern().matcher(modifiedMessage)
												.replaceAll("***"));
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
								final String warnMessage = messagesModule.getWarnMessage(placeholders, lang,
										module.getName());

								if (warnMessage != null && !warnMessage.isEmpty())
									player.sendMessage(new TextComponent(warnMessage));

								if (notificationMessage != null && !notificationMessage.isEmpty()) {
									for (final ProxiedPlayer player1 : server.getPlayers()) {
										if (player1.hasPermission("chatsentinel.notify"))
											player1.sendMessage(new TextComponent(notificationMessage));
									}

									server.getConsole().sendMessage(new TextComponent(notificationMessage));
								}

								if (warns >= maxWarns && maxWarns > 0) {
									final PluginManager pluginManager = server.getPluginManager();

									for (final String command : module.getCommands(placeholders)) {
										pluginManager.dispatchCommand(server.getConsole(), command);
									}
								}

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
		}
	}

	private String formatMessage(final String string) {
		return Normalizer.normalize(string.replace("[(]?punto[)]?", ".").replace("[(]?dot[)]?", "."),
				Normalizer.Form.NFKD);
	}
}
