package twolovers.chatsentinel.bungee.commands;

import java.util.Locale;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import twolovers.chatsentinel.shared.modules.MessagesModule;
import twolovers.chatsentinel.bungee.modules.ModuleManager;

public class ChatSentinelCommand extends Command {
	final private ModuleManager moduleManager;

	public ChatSentinelCommand(final ModuleManager moduleManager) {
		super("chatsentinel");
		this.moduleManager = moduleManager;
	}

	@Override
	public void execute(final CommandSender commandSender, final String[] args) {
		final MessagesModule messagesModule = moduleManager.getMessagesModule();
		final String lang;

		if (commandSender instanceof ProxiedPlayer) {
			final Locale locale = ((ProxiedPlayer) commandSender).getLocale();

			if (locale != null)
				lang = locale.toLanguageTag();
			else
				lang = null;
		} else
			lang = null;

		if (!commandSender.hasPermission("chatsentinel.admin")) {
			commandSender.sendMessage(new TextComponent(messagesModule.getNoPermission(lang)));
		} else if (args.length == 0) {
			commandSender.sendMessage(new TextComponent(messagesModule.getHelp(lang)));
		} else if (args[0].equalsIgnoreCase("reload")) {
			moduleManager.reloadData();

			commandSender.sendMessage(new TextComponent(messagesModule.getReload(lang)));
		} else
			commandSender.sendMessage(new TextComponent(messagesModule.getUnknownCommand(lang)));
	}
}
