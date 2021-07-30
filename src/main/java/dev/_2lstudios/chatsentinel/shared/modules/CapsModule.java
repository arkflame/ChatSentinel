package dev._2lstudios.chatsentinel.shared.modules;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class CapsModule implements Module {
	private boolean enabled, replace;
	private int max, maxWarns;
	private String warnNotification;
	private String[] commands;

	final public void loadData(final boolean enabled, final boolean replace, final int max, final int maxWarns,
			final String warnNotification, final String[] commands) {
		this.enabled = enabled;
		this.replace = replace;
		this.max = max;
		this.maxWarns = maxWarns;
		this.warnNotification = warnNotification;
		this.commands = commands;
	}

	final public boolean isReplace() {
		return this.replace;
	}

	final public long capsCount(final String string) {
		return string.codePoints().filter(c -> c >= 'A' && c <= 'Z').count();
	}

	@Override
	final public boolean meetsCondition(final ChatPlayer chatPlayer, final String message) {
		if (this.enabled && this.capsCount(message) > max)
			return true;

		return false;
	}

	@Override
	final public String getName() {
		return "Caps";
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
