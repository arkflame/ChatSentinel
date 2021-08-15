package dev._2lstudios.chatsentinel.bungee.listeners;

import java.util.UUID;
import java.util.regex.Pattern;

import dev._2lstudios.chatsentinel.bungee.modules.ModuleManager;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.modules.BlacklistModule;
import dev._2lstudios.chatsentinel.shared.modules.CapsModule;
import dev._2lstudios.chatsentinel.shared.modules.CooldownModule;
import dev._2lstudios.chatsentinel.shared.modules.FloodModule;
import dev._2lstudios.chatsentinel.shared.modules.MessagesModule;
import dev._2lstudios.chatsentinel.shared.modules.SyntaxModule;
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;
import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;
import dev._2lstudios.chatsentinel.shared.utils.StringUtil;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;
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
					final GeneralModule generalModule = moduleManager.getGeneralModule();
					final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
					final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);
					final String originalMessage = event.getMessage().trim();
					final boolean isCommand = event.isCommand();
					final boolean isNormalCommand = generalModule.isCommand(originalMessage);
					String modifiedMessage;

					modifiedMessage = StringUtil.removeAccents(originalMessage);
		
					if (isCommand()) {
						if (originalMessage.contains(" ")) {
							if (isNormalCommand) {
								modifiedMessage = modifiedMessage.replace("/", "");
							}
						} else {
							modifiedMessage = "/";
						}
					}
		
					if (whitelistModule.isEnabled()) {
						final Pattern whitelistPattern = whitelistModule.getPattern();
		
						modifiedMessage = whitelistPattern.matcher(modifiedMessage).replaceAll("");
					}

					final MessagesModule messagesModule = moduleManager.getMessagesModule();
					final ProxyServer server = plugin.getProxy();
					final String playerName = player.getName(), lang = VersionUtil.getLocale(player);

					for (final Module module : moduleManager.getModules()) {
						if (!player.hasPermission("chatsentinel.bypass." + module.getName())
								&& (!isCommand || module instanceof CooldownModule || module instanceof SyntaxModule
										|| isNormalCommand)
								&& module.meetsCondition(chatPlayer, modifiedMessage)) {
							final int warns = chatPlayer.addWarn(module), maxWarns = module.getMaxWarns();
							final String[][] placeholders = {
									{ "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%" },
									{ playerName, originalMessage, String.valueOf(warns),
											String.valueOf(module.getMaxWarns()), String.valueOf(0) } };

							if (module instanceof BlacklistModule) {
								final BlacklistModule blacklistModule = (BlacklistModule) module;

								if (blacklistModule.isHideWords()) {
									event.setMessage(
											blacklistModule.getPattern().matcher(modifiedMessage).replaceAll("***"));
								} else
									event.setCancelled(true);
							} else if (module instanceof CapsModule) {
								final CapsModule capsModule = (CapsModule) module;

								if (capsModule.isReplace())
									event.setMessage(originalMessage.toLowerCase());
								else
									event.setCancelled(true);
							} else if (module instanceof CooldownModule) {
								placeholders[1][4] = String.valueOf(
										((CooldownModule) module).getRemainingTime(chatPlayer, originalMessage));

								event.setCancelled(true);
							} else if (module instanceof FloodModule) {
								final FloodModule floodModule = (FloodModule) module;

								if (floodModule.isReplace()) {
									final String replacedString = floodModule.replace(originalMessage);

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

							final CommandSender console = server.getConsole();
							final String notificationMessage = module.getWarnNotification(placeholders);
							final String warnMessage = messagesModule.getWarnMessage(placeholders, lang,
									module.getName());

							if (warnMessage != null && !warnMessage.isEmpty()) {
								player.sendMessage(TextComponent.fromLegacyText(warnMessage));
							}

							if (notificationMessage != null && !notificationMessage.isEmpty()) {
								for (final ProxiedPlayer player1 : server.getPlayers()) {
									if (player1.hasPermission("chatsentinel.notify")) {
										player1.sendMessage(TextComponent.fromLegacyText(notificationMessage));
									}
								}

								console.sendMessage(TextComponent.fromLegacyText(notificationMessage));
							}

							if (warns >= maxWarns && maxWarns > 0) {
								final PluginManager pluginManager = server.getPluginManager();

								for (final String command : module.getCommands(placeholders)) {
									pluginManager.dispatchCommand(console, command);
								}

								chatPlayer.clearWarns();
							}

							if (event.isCancelled()) {
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
}
