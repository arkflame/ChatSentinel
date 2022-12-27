package dev._2lstudios.chatsentinel.shared.modules;

import java.util.regex.Pattern;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class FloodModule implements Module {
	private boolean enabled, replace;
	private int maxWarns;
	private String warnNotification;
	private String[] commands;
	private Pattern pattern;

	public void loadData(boolean enabled, boolean replace, int maxWarns, String pattern,
			String warnNotification, String[] commands) {
		this.enabled = enabled;
		this.replace = replace;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
		this.commands = commands;
		this.pattern = Pattern.compile(pattern);
	}

	public boolean isReplace() {
		return this.replace;
	}

	public String replace(String string) {
		return pattern.matcher(string).replaceAll("");
	}

	@Override
	public boolean meetsCondition(ChatPlayer chatPlayer, String message) {
		return this.enabled && pattern.matcher(message).find();
	}

	@Override
	public String getName() {
		return "Flood";
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
