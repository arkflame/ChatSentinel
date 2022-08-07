package dev._2lstudios.chatsentinel.shared.modules;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class SyntaxModule implements Module {
	private boolean enabled;
	private int maxWarns;
	private String warnNotification;
	private String[] whitelist, commands;

	final public void loadData(final boolean enabled, final int maxWarns, final String warnNotification,
			final String[] whitelist, final String[] commands) {
		this.enabled = enabled;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
		this.whitelist = whitelist;
		this.commands = commands;
	}

	final public boolean isWhitelisted(final String message) {
		if (whitelist.length > 0)
			for (final String string : whitelist)
				if (message.startsWith(string))
					return true;

		return false;
	}

	@Override
	public boolean meetsCondition(final ChatPlayer chatPlayer, final String message) {
		return (enabled && !isWhitelisted(message) && hasSyntax(message));
	}

	@Override
	final public String getName() {
		return "Syntax";
	}

	@Override
	final public String[] getCommands(final String[][] placeholders) {
		if (this.commands.length > 0) {
			final String[] commands = this.commands.clone();

			for (int i = 0; i < commands.length; i++) {
				commands[i] = PlaceholderUtil.replacePlaceholders(commands[i], placeholders);
			}

			return commands;
		} else
			return new String[0];
	}

	@Override
	final public String getWarnNotification(final String[][] placeholders) {
		if (!this.warnNotification.isEmpty()) {
			return PlaceholderUtil.replacePlaceholders(this.warnNotification, placeholders);
		} else
			return null;
	}

	@Override
	public int getMaxWarns() {
		return maxWarns;
	}

	private boolean hasSyntax(final String message) {
		if (message.startsWith("/")) {
			final String command;

			if (message.contains(" ")) {
				command = message.split(" ")[0];
			} else {
				command = message;
			}

			final String[] syntax = command.split(":");

			if (syntax.length > 1)
				return true;
		}

		return false;
	}
}
