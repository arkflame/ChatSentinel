package dev._2lstudios.chatsentinel.shared.modules;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class SyntaxModule implements Module {
	private boolean enabled;
	private int maxWarns;
	private String warnNotification;
	private String[] whitelist, commands;

	public void loadData(boolean enabled, int maxWarns, String warnNotification,
			String[] whitelist, String[] commands) {
		this.enabled = enabled;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
		this.whitelist = whitelist;
		this.commands = commands;
	}

	public boolean isWhitelisted(String message) {
		if (whitelist.length > 0)
			for (String string : whitelist)
				if (message.startsWith(string))
					return true;

		return false;
	}

	@Override
	public boolean meetsCondition(ChatPlayer chatPlayer, String message) {
		return (enabled && !isWhitelisted(message) && hasSyntax(message));
	}

	@Override
	public String getName() {
		return "Syntax";
	}

	@Override
	public String[] getCommands(String[][] placeholders) {
		if (this.commands.length > 0) {
			String[] commands = this.commands.clone();

			for (int i = 0; i < commands.length; i++) {
				commands[i] = PlaceholderUtil.replacePlaceholders(commands[i], placeholders);
			}

			return commands;
		} else
			return new String[0];
	}

	@Override
	public String getWarnNotification(String[][] placeholders) {
		if (!this.warnNotification.isEmpty()) {
			return PlaceholderUtil.replacePlaceholders(this.warnNotification, placeholders);
		} else
			return null;
	}

	@Override
	public int getMaxWarns() {
		return maxWarns;
	}

	private boolean hasSyntax(String message) {
		if (message.startsWith("/")) {
			String command;

			if (message.contains(" ")) {
				command = message.split(" ")[0];
			} else {
				command = message;
			}

			String[] syntax = command.split(":");

			if (syntax.length > 1)
				return true;
		}

		return false;
	}
}
