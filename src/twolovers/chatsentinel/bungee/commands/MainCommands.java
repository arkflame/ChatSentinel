package twolovers.chatsentinel.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import twolovers.chatsentinel.bungee.variables.MessagesVariables;
import twolovers.chatsentinel.bungee.variables.PluginVariables;

public class MainCommands extends Command {
	final private PluginVariables pluginVariables;

	public MainCommands(final PluginVariables pluginVariables) {
		super("chatsentinel");
		this.pluginVariables = pluginVariables;
	}

	@Override
	public void execute(final CommandSender commandSender, final String[] args) {
		final MessagesVariables messagesVariables = pluginVariables.getMessagesVariables();

		if (!commandSender.hasPermission("chatsentinel.admin")) {
			commandSender.sendMessage(new TextComponent(messagesVariables.getNoPermission()));
			return;
		}

		if (args.length == 0) {
			commandSender.sendMessage(new TextComponent(messagesVariables.getUsageMessage()));
			return;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			pluginVariables.reloadData();

			commandSender.sendMessage(new TextComponent(messagesVariables.getReloadMessage()));
			return;
		}

		commandSender.sendMessage(new TextComponent(messagesVariables.getUnknownCommand()));
	}
}
