package twolovers.chatsentinel.shared.modules;

import java.util.regex.Pattern;

import twolovers.chatsentinel.shared.chat.ChatPlayer;
import twolovers.chatsentinel.shared.interfaces.Module;
import twolovers.chatsentinel.shared.utils.PlaceholderUtil;

public class FloodModule implements Module {
	private boolean enabled, replace;
	private int maxWarns;
	private String warnNotification;
	private String[] commands;
	private Pattern pattern;

	final public void loadData(final boolean enabled, final boolean replace, final int maxWarns, final String pattern,
			final String warnNotification, final String[] commands) {
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

	final public String replacePattern(final String string) {
		return pattern.matcher(string).replaceAll("");
	}

	@Override
	public boolean meetsCondition(final ChatPlayer chatPlayer, final String message) {
		return this.enabled && pattern.matcher(message).find();
	}

	@Override
	final public String getName() {
		return "Flood";
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
