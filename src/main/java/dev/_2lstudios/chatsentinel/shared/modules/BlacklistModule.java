package dev._2lstudios.chatsentinel.shared.modules;

import java.util.regex.Pattern;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.utils.PatternUtil;
import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class BlacklistModule implements Module {
	private boolean enabled, fakeMessage, hideWords, blockRawMessage;
	private int maxWarns;
	private String warnNotification;
	private String[] commands;
	private Pattern pattern;

	public void loadData(boolean enabled, boolean fakeMessage, boolean hideWords, int maxWarns,
			String warnNotification, String[] commands, String[] patterns, boolean blockRawMessage) {
		this.enabled = enabled;
		this.fakeMessage = fakeMessage;
		this.hideWords = hideWords;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
		this.commands = commands;
		this.pattern = PatternUtil.compile(patterns);
		this.blockRawMessage = blockRawMessage;
	}

	public boolean isFakeMessage() {
		return this.fakeMessage;
	}

	public boolean isHideWords() {
		return this.hideWords;
	}

	public boolean isBlockRawMessage() {
		return this.blockRawMessage;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public boolean meetsCondition(ChatPlayer chatPlayer, String message) {
		message = message.startsWith("/") && message.contains(" ") ? message.substring(message.indexOf(" ")) : message;

		return enabled && pattern.matcher(message).find();
	}

	@Override
	public String getName() {
		return "Blacklist";
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
}
