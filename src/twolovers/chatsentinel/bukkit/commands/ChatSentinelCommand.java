package twolovers.chatsentinel.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import twolovers.chatsentinel.bukkit.modules.ModuleManager;
import twolovers.chatsentinel.bukkit.utils.VersionUtil;
import twolovers.chatsentinel.shared.modules.MessagesModule;

public class ChatSentinelCommand implements CommandExecutor {
	final private ModuleManager moduleManager;

	public ChatSentinelCommand(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		final MessagesModule messagesModule = moduleManager.getMessagesModule();
		final String lang;

		if (sender instanceof Player) {
			if (VersionUtil.isOneDotNine()) {
				lang = ((Player) sender).getLocale();
			} else
				lang = ((Player) sender).spigot().getLocale();
		} else
			lang = null;

		if (sender.hasPermission("chatsentinel.admin")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("help"))
				sender.sendMessage(messagesModule.getHelp(lang));
			else if (args[0].equalsIgnoreCase("reload")) {
				moduleManager.reloadData();

				sender.sendMessage(messagesModule.getReload(lang));
			} else
				sender.sendMessage(messagesModule.getUnknownCommand(lang));
		} else
			sender.sendMessage(messagesModule.getNoPermission(lang));

		return true;
	}
}
