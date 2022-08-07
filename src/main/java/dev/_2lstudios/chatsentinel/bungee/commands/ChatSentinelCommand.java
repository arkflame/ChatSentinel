package dev._2lstudios.chatsentinel.bungee.commands;

import dev._2lstudios.chatsentinel.bungee.modules.BungeeModuleManager;
import dev._2lstudios.chatsentinel.shared.modules.MessagesModule;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChatSentinelCommand extends Command {
	private final BungeeModuleManager moduleManager;
	private final ProxyServer server;

	public ChatSentinelCommand(final BungeeModuleManager moduleManager, final ProxyServer server) {
		super("chatsentinel");
		this.moduleManager = moduleManager;
		this.server = server;
	}

	private void sendMessage(final CommandSender sender, final String message) {
		sender.sendMessage(TextComponent.fromLegacyText(message));
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		final MessagesModule messagesModule = moduleManager.getMessagesModule();
		final String lang;

		if (sender instanceof ProxiedPlayer) {
			lang = VersionUtil.getLocale((ProxiedPlayer) sender);
		} else {
			lang = "en";
		}

		if (sender.hasPermission("chatsentinel.admin")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
				sendMessage(sender, messagesModule.getHelp(lang));
			} else if (args[0].equalsIgnoreCase("reload")) {
				moduleManager.reloadData();

				sendMessage(sender, messagesModule.getReload(lang));
			} else if (args[0].equalsIgnoreCase("clear")) {
				final StringBuilder emptyLines = new StringBuilder();
				final String newLine = "\n ";
				final String[][] placeholders = { { "%player%" }, { sender.getName() } };

				for (int i = 0; i < 128; i++) {
					emptyLines.append(newLine);
				}

				emptyLines.append(messagesModule.getCleared(placeholders, lang));

				for (final ProxiedPlayer player : server.getPlayers()) {
					sendMessage(player, emptyLines.toString());
				}
			} else {
				sendMessage(sender, messagesModule.getUnknownCommand(lang));
			}
		} else {
			sendMessage(sender, messagesModule.getNoPermission(lang));
		}
	}
}
