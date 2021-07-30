package dev._2lstudios.chatsentinel.shared.modules;

import java.util.regex.Pattern;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class BlacklistModule implements Module {
	private boolean enabled, fakeMessage, hideWords;
	private int maxWarns;
	private String warnNotification;
	private String[] commands;
	private Pattern pattern;

	final public void loadData(final boolean enabled, final boolean fakeMessage, final boolean hideWords,
			final int maxWarns, final String warnNotification, final String[] commands, final String[] patterns) {
		String patternString = "";

		for (final String string : patterns) {
			patternString = String.format("%s(%s)|", patternString, string);
		}

		this.enabled = enabled;
		this.fakeMessage = fakeMessage;
		this.hideWords = hideWords;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
		this.commands = commands;
		this.pattern = Pattern.compile("(?i)(" + patternString + "(?!x)x)");
	}

	final public boolean isFakeMessage() {
		return this.fakeMessage;
	}

	final public boolean isHideWords() {
		return this.hideWords;
	}

	final public Pattern getPattern() {
		return pattern;
	}

	@Override
	public boolean meetsCondition(final ChatPlayer chatPlayer, String message) {
		message = message.startsWith("/") && message.contains(" ") ? message.substring(message.indexOf(" ")) : message;

		return enabled && pattern.matcher(message).find();
	}

	@Override
	public String getName() {
		return "Blacklist";
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
}
