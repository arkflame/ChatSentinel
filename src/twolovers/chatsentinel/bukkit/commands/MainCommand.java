package twolovers.chatsentinel.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import twolovers.chatsentinel.bukkit.variables.MessagesVariables;
import twolovers.chatsentinel.bukkit.variables.PluginVariables;

public class MainCommand implements CommandExecutor {
	final private PluginVariables pluginVariables;

	public MainCommand(final PluginVariables pluginVariables) {
		this.pluginVariables = pluginVariables;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		final MessagesVariables messagesVariables = pluginVariables.getMessagesVariables();

		if (sender.hasPermission("chatsentinel.admin")) {
			if (args.length == 0 || args[0].equalsIgnoreCase("help"))
				sender.sendMessage(messagesVariables.getUsageMessage());
			else if (args[0].equalsIgnoreCase("reload")) {
				pluginVariables.reloadData();

				sender.sendMessage(messagesVariables.getReloadMessage());
			} else
				sender.sendMessage(messagesVariables.getUnknownCommand());
		} else
			sender.sendMessage(messagesVariables.getNoPermission());

		return true;
	}
}
