package dev._2lstudios.chatsentinel.bukkit.commands;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev._2lstudios.chatsentinel.bukkit.modules.BukkitModuleManager;
import dev._2lstudios.chatsentinel.shared.modules.MessagesModule;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;

public class ChatSentinelCommand implements CommandExecutor {
	private BukkitModuleManager moduleManager;
	private Server server;

	public ChatSentinelCommand(BukkitModuleManager moduleManager, Server server) {
		this.moduleManager = moduleManager;
		this.server = server;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		MessagesModule messagesModule = moduleManager.getMessagesModule();
		String lang;

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
				StringBuilder emptyLines = new StringBuilder();
				String newLine = "\n ";
				String[][] placeholders = { { "%player%" }, { sender.getName() } };

				for (int i = 0; i < 128; i++) {
					emptyLines.append(newLine);
				}

				emptyLines.append(messagesModule.getCleared(placeholders, lang));

				for (Player player : server.getOnlinePlayers()) {
					player.sendMessage(emptyLines.toString());
				}
			} else {
				sender.sendMessage(messagesModule.getUnknownCommand(lang));
			}
		} else {
			sender.sendMessage(messagesModule.getNoPermission(lang));
		}

		return true;
	}
}
