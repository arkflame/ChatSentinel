package twolovers.chatsentinel.bukkit.commands;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import twolovers.chatsentinel.bukkit.modules.ModuleManager;
import twolovers.chatsentinel.shared.modules.MessagesModule;
import twolovers.chatsentinel.shared.utils.VersionUtil;

public class ChatSentinelCommand implements CommandExecutor {
	private final ModuleManager moduleManager;
	private final Server server;

	public ChatSentinelCommand(final ModuleManager moduleManager, final Server server) {
		this.moduleManager = moduleManager;
		this.server = server;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		final MessagesModule messagesModule = moduleManager.getMessagesModule();
		final String lang;

		if (sender instanceof Player) {
			lang = VersionUtil.getLocale((Player) sender);
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
				final String newLine = "\n";

				for (int i = 0; i < 32; i++) {
					emptyLines.append(newLine);
				}

				server.broadcast(emptyLines.toString(), "");
			} else {
				sender.sendMessage(messagesModule.getUnknownCommand(lang));
			}
		} else {
			sender.sendMessage(messagesModule.getNoPermission(lang));
		}

		return true;
	}
}
