package dev._2lstudios.chatsentinel.velocity.listeners;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.modules.*;
import dev._2lstudios.chatsentinel.shared.utils.StringUtil;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;
import dev._2lstudios.chatsentinel.velocity.ChatSentinel;
import dev._2lstudios.chatsentinel.velocity.modules.ModuleManager;
import net.kyori.adventure.text.Component;

import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener {
	final private ProxyServer proxyServer;
	final private ModuleManager moduleManager;
	final private ChatPlayerManager chatPlayerManager;

	public ChatListener(final ProxyServer proxyServer, final ModuleManager moduleManager,
                        final ChatPlayerManager chatPlayerManager) {
		this.proxyServer = proxyServer;
		this.moduleManager = moduleManager;
		this.chatPlayerManager = chatPlayerManager;
	}

	private void sendMessage(final CommandSource source, final String message) {
		source.sendMessage(Component.text(message.replace('&', '§')));
	}

	@Subscribe
	public void onChat(PlayerChatEvent event) {
		if (event.getResult().isAllowed()) {
			final Player player = event.getPlayer();

			if (!player.hasPermission("chatsentinel.bypass")) {
				final UUID uuid = player.getUniqueId();
				final GeneralModule generalModule = moduleManager.getGeneralModule();
				final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
				final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);
				final String originalMessage = event.getMessage().trim();
				final boolean isCommand = event.getMessage().startsWith("/");
				final boolean isNormalCommand = generalModule.isCommand(originalMessage);
				String modifiedMessage;

				modifiedMessage = StringUtil.removeAccents(originalMessage);

				if (isCommand) {
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
				final String playerName = player.getUsername(), lang = VersionUtil.getLocale(player);

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
								event.setResult(PlayerChatEvent.ChatResult.message(blacklistModule.getPattern().matcher(modifiedMessage).replaceAll("***")));
							} else
								event.setResult(PlayerChatEvent.ChatResult.denied());
						} else if (module instanceof CapsModule) {
							final CapsModule capsModule = (CapsModule) module;

							if (capsModule.isReplace())
								event.setResult(PlayerChatEvent.ChatResult.message(originalMessage.toLowerCase()));

							else
								event.setResult(PlayerChatEvent.ChatResult.denied());
						} else if (module instanceof CooldownModule) {
							placeholders[1][4] = String.valueOf(
									((CooldownModule) module).getRemainingTime(chatPlayer, originalMessage));

							event.setResult(PlayerChatEvent.ChatResult.denied());
						} else if (module instanceof FloodModule) {
							final FloodModule floodModule = (FloodModule) module;

							if (floodModule.isReplace()) {
								final String replacedString = floodModule.replace(originalMessage);

								if (!replacedString.isEmpty()) {
									event.setResult(PlayerChatEvent.ChatResult.message(replacedString));
								} else {
									event.setResult(PlayerChatEvent.ChatResult.denied());
								}
							} else {
								event.setResult(PlayerChatEvent.ChatResult.denied());
							}
						} else {
							event.setResult(PlayerChatEvent.ChatResult.denied());
						}

						final ConsoleCommandSource console = proxyServer.getConsoleCommandSource();
						final String notificationMessage = module.getWarnNotification(placeholders);
						final String warnMessage = messagesModule.getWarnMessage(placeholders, lang,
								module.getName());

						if (warnMessage != null && !warnMessage.isEmpty()) {
							player.sendMessage(Component.text(warnMessage));
						}

						if (notificationMessage != null && !notificationMessage.isEmpty()) {
							for (final Player player1 : proxyServer.getAllPlayers()) {
								if (player1.hasPermission("chatsentinel.notify")) {
									sendMessage(player1, notificationMessage);
								}
							}
							sendMessage(console, notificationMessage);
						}

						if (warns >= maxWarns && maxWarns > 0) {
							for (final String command : module.getCommands(placeholders)) {
								proxyServer.getCommandManager().executeAsync(console, command);
							}

							chatPlayer.clearWarns();
						}

						if (!event.getResult().isAllowed()) {
							break;
						}
					}
				}

				if (event.getResult().isAllowed()) {
					final long currentMillis = System.currentTimeMillis();

					chatPlayer.addLastMessage(modifiedMessage, currentMillis);
					moduleManager.getCooldownModule().setLastMessage(modifiedMessage, currentMillis);
				}
			}

		}
	}
}
