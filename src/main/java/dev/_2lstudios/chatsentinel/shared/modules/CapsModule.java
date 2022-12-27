package dev._2lstudios.chatsentinel.shared.modules;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class CapsModule implements Module {
	private boolean enabled, replace;
	private int max, maxWarns;
	private String warnNotification;
	private String[] commands;

	public void loadData(boolean enabled, boolean replace, int max, int maxWarns,
			String warnNotification, String[] commands) {
		this.enabled = enabled;
		this.replace = replace;
		this.max = max;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
		this.commands = commands;
	}

	public boolean isReplace() {
		return this.replace;
	}

	public long capsCount(String string) {
		return string.codePoints().filter(c -> c >= 'A' && c <= 'Z').count();
	}

	@Override
	public boolean meetsCondition(ChatPlayer chatPlayer, String message) {
		if (this.enabled && this.capsCount(message) > max)
			return true;

		return false;
	}

	@Override
	public String getName() {
		return "Caps";
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
