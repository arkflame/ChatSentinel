package twolovers.chatsentinel.bungee.listeners;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import twolovers.chatsentinel.bungee.variables.*;

import java.util.regex.Pattern;

public class ChatListener implements Listener {
	final private PluginVariables pluginVariables;
	private final FloodVariables floodVariables;
	private final PatternVariables patternVariables;
	private final SwearingVariables swearingVariables;
	private final ThrottleVariables cooldownVariables;
	private final SyntaxVariables syntaxVariables;

	public ChatListener(final PluginVariables pluginVariables) {
		this.pluginVariables = pluginVariables;
		floodVariables = pluginVariables.getFloodVariables();
		patternVariables = pluginVariables.getPatternVariables();
		swearingVariables = pluginVariables.getSwearingVariables();
		cooldownVariables = pluginVariables.getThrottleVariables();
		syntaxVariables = pluginVariables.getSyntaxVariables();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChatEvent(final ChatEvent event) {
		if (event.getSender() instanceof ProxiedPlayer) {
			final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();

			if (!proxiedPlayer.hasPermission("chatsentinel.bypass")) {
				final String message = event.getMessage();
				String translatedMessage = translateCharacters(message);
				final boolean isCommand = translatedMessage.startsWith("/") || event.isCommand();
				final long currentTimeMillis = System.currentTimeMillis();
				final long throttleTime = currentTimeMillis - pluginVariables.getThrottle(proxiedPlayer);
				final Pattern whitelistPattern = patternVariables.getWhitelistPattern();

				if (whitelistPattern != null) {
					translatedMessage = whitelistPattern.matcher(translatedMessage).replaceAll(" ");
					translatedMessage = patternVariables.getNamesPattern().matcher(translatedMessage).replaceAll(" ");
					translatedMessage = translatedMessage.trim();
				}

				if (swearingVariables.isSwearingEnabled() && !translatedMessage.isEmpty() && (!isCommand || patternVariables.startsWithCommand(message)) && patternVariables.getBlacklistPattern().matcher(translatedMessage).find()) {
					pluginVariables.addWarn(proxiedPlayer);

					final int warns = pluginVariables.getWarns(proxiedPlayer);
					final String proxiedPlayerName = proxiedPlayer.getName();
					final String swearingWarnMessage = swearingVariables.getSwearingWarnMessage().replace("%warns%", String.valueOf(warns));
					final String swearingPunishCommand = swearingVariables.getSwearingPunishCommand().replace("%player%", proxiedPlayerName).replace("%message%", translatedMessage);
					final String swearingWarnNotification = swearingVariables.getSwearingWarnNotification().replace("%player%", proxiedPlayerName).replace("%message%", translatedMessage);

					if (!swearingWarnMessage.isEmpty())
						proxiedPlayer.sendMessage(new TextComponent(swearingWarnMessage));

					if (swearingVariables.isFakeMessage()) {
						proxiedPlayer.sendMessage(new TextComponent(message));
						event.setCancelled(true);
					} else if (swearingVariables.isHideWords())
						event.setMessage(message.replaceAll(patternVariables.getBlacklistPattern().toString(), "***"));
					else
						event.setCancelled(true);

					if (warns == swearingVariables.getMaxWarnings() && !swearingPunishCommand.equals("")) {
						final BungeeCord bungeeCord = BungeeCord.getInstance();

						pluginVariables.removeWarns(proxiedPlayer);
						bungeeCord.getPluginManager().dispatchCommand(bungeeCord.getConsole(), swearingPunishCommand);
					}

					if (!swearingWarnNotification.isEmpty()) {
						for (final ProxiedPlayer proxiedPlayer1 : BungeeCord.getInstance().getPlayers()) {
							if (proxiedPlayer1.hasPermission("chatsentinel.notify")) {
								proxiedPlayer1.sendMessage(new TextComponent(swearingWarnNotification));
							}
						}

						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(swearingWarnNotification));
					}
				} else if (!isCommand) {
					if (cooldownVariables.isEnabled() && throttleTime < cooldownVariables.getTime()) {
						proxiedPlayer.sendMessage(new TextComponent(cooldownVariables.getWarnMessage()));
						event.setCancelled(true);
					} else if (floodVariables.isEnabled() && !proxiedPlayer.hasPermission("chatsentinel.bypass.flood") && throttleTime < floodVariables.getTime() && translatedMessage.contains(pluginVariables.getLastMessage(proxiedPlayer))) {
						proxiedPlayer.sendMessage(new TextComponent(floodVariables.getFloodWarnMessage()));
						event.setCancelled(true);
					} else {
						pluginVariables.setLastMessage(proxiedPlayer, translatedMessage);
						pluginVariables.setThrottle(proxiedPlayer, currentTimeMillis);
					}
				} else if (syntaxVariables.isSyntaxEnabled() && syntaxVariables.getSyntaxPattern().matcher(message).find()) {
					proxiedPlayer.sendMessage(new TextComponent(syntaxVariables.getSyntaxWarnMessage()));
					event.setCancelled(true);
				}
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
