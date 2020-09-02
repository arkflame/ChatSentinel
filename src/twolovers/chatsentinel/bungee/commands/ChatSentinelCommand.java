package twolovers.chatsentinel.bungee.commands;

import java.util.Locale;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import twolovers.chatsentinel.shared.modules.MessagesModule;
import twolovers.chatsentinel.bungee.modules.ModuleManager;

public class ChatSentinelCommand extends Command {
	private final ModuleManager moduleManager;
	private final ProxyServer server;

	public ChatSentinelCommand(final ModuleManager moduleManager, final ProxyServer server) {
		super("chatsentinel");
		this.moduleManager = moduleManager;
		this.server = server;
	}

	private String getLocale(final CommandSender sender) {
		if (sender instanceof ProxiedPlayer) {
			final Locale locale = ((ProxiedPlayer) sender).getLocale();

			if (locale != null) {
				return locale.toString().substring(0, 2);
			}
		}

		return null;
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		final MessagesModule messagesModule = moduleManager.getMessagesModule();
		final String lang = getLocale(sender);

		if (sender.hasPermission("chatsentinel.admin")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(messagesModule.getHelp(lang));
			} else if (args[0].equalsIgnoreCase("reload")) {
				moduleManager.reloadData();

				sender.sendMessage(messagesModule.getReload(lang));
			} else if (args[0].equalsIgnoreCase("clear")) {
				final StringBuilder emptyLines = new StringBuilder();
				final String newLine = "\n";

				for (int i = 0; i < 32; i++) {
					emptyLines.append(newLine);
				}

				server.broadcast(emptyLines.toString());
			} else {
				sender.sendMessage(messagesModule.getUnknownCommand(lang));
			}
		} else {
			sender.sendMessage(messagesModule.getNoPermission(lang));
		}
	}
}
