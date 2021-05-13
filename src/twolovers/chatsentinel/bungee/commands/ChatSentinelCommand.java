package twolovers.chatsentinel.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import twolovers.chatsentinel.shared.modules.MessagesModule;
import twolovers.chatsentinel.shared.utils.VersionUtil;
import twolovers.chatsentinel.bungee.modules.ModuleManager;

public class ChatSentinelCommand extends Command {
	private final ModuleManager moduleManager;
	private final ProxyServer server;

	public ChatSentinelCommand(final ModuleManager moduleManager, final ProxyServer server) {
		super("chatsentinel");
		this.moduleManager = moduleManager;
		this.server = server;
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
				sender.sendMessage(messagesModule.getHelp(lang));
			} else if (args[0].equalsIgnoreCase("reload")) {
				moduleManager.reloadData();

				sender.sendMessage(messagesModule.getReload(lang));
			} else if (args[0].equalsIgnoreCase("clear")) {
				final StringBuilder emptyLines = new StringBuilder();
				final String newLine = "\n ";
				final String[][] placeholders = { { "%player%" }, { sender.getName() } };

				for (int i = 0; i < 128; i++) {
					emptyLines.append(newLine);
				}

				emptyLines.append(messagesModule.getCleared(placeholders, lang));

				for (final ProxiedPlayer player : server.getPlayers()) {
					player.sendMessage(emptyLines.toString());
				}
			} else {
				sender.sendMessage(messagesModule.getUnknownCommand(lang));
			}
		} else {
			sender.sendMessage(messagesModule.getNoPermission(lang));
		}
	}
}
