package dev._2lstudios.chatsentinel.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev._2lstudios.chatsentinel.shared.modules.MessagesModule;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;
import dev._2lstudios.chatsentinel.velocity.modules.ModuleManager;
import net.kyori.adventure.text.Component;

public class ChatSentinelCommand implements SimpleCommand {
	private final ModuleManager moduleManager;
	private final ProxyServer server;

	public ChatSentinelCommand(final ModuleManager moduleManager, final ProxyServer server) {
		this.moduleManager = moduleManager;
		this.server = server;
	}

	private void sendMessage(final CommandSource source, final String message) {
		source.sendMessage(Component.text(message.replace('&', '§')));
	}

	@Override
	public void execute(Invocation invocation) {
		final MessagesModule messagesModule = moduleManager.getMessagesModule();
		final String lang;

		CommandSource source = invocation.source();
		String[] args = invocation.arguments();

		if (source instanceof Player) {
			lang = VersionUtil.getLocale((Player) source);
		} else {
			lang = "en";
		}

		String name = source instanceof Player ? ((Player) source).getUsername() : "CONSOLE";

		if (source.hasPermission("chatsentinel.admin")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
				sendMessage(source, messagesModule.getHelp(lang));
			} else if (args[0].equalsIgnoreCase("reload")) {
				moduleManager.reloadData();

				sendMessage(source, messagesModule.getReload(lang));
			} else if (args[0].equalsIgnoreCase("clear")) {
				final StringBuilder emptyLines = new StringBuilder();
				final String newLine = "\n ";
				final String[][] placeholders = { { "%player%" }, { name } };

				for (int i = 0; i < 128; i++) {
					emptyLines.append(newLine);
				}

				emptyLines.append(messagesModule.getCleared(placeholders, lang));

				for (final Player eachPlayer : server.getAllPlayers()) {
					sendMessage(eachPlayer, emptyLines.toString());
				}
			} else {
				sendMessage(source, messagesModule.getUnknownCommand(lang));
			}
		} else {
			sendMessage(source, messagesModule.getNoPermission(lang));
		}
	}
}
