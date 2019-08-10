package twolovers.chatsentinel.bukkit.variables;

import org.bukkit.configuration.Configuration;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;

public class SwearingVariables {
	final private ConfigUtil configUtil;
	private boolean swearingEnabled;
	private String swearingWarnMessage;
	private String swearingWarnNotification;
	private String swearingPunishCommand;
	private int maxWarnings;
	private boolean fakeMessage;
	private boolean hideWords;

	public SwearingVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");

		swearingEnabled = configYml.getBoolean("swearing.enabled");
		swearingWarnMessage = configYml.getString("swearing.warn_message").replace("&", "§");
		swearingWarnNotification = configYml.getString("swearing.warn_notification").replace("&", "§");
		swearingPunishCommand = configYml.getString("swearing.punish_command");
		maxWarnings = configYml.getInt("swearing.max_warnings");
		fakeMessage = configYml.getBoolean("swearing.fake_message");
		hideWords = configYml.getBoolean("swearing.hide_words");
	}

	final public boolean isSwearingEnabled() {
		return swearingEnabled;
	}

	final public String getSwearingWarnMessage() {
		return swearingWarnMessage;
	}

	final public String getSwearingWarnNotification() {
		return swearingWarnNotification;
	}

	final public String getSwearingPunishCommand() {
		return swearingPunishCommand;
	}

	final public int getMaxWarnings() {
		return maxWarnings;
	}

	final public boolean isFakeMessage() {
		return fakeMessage;
	}

	public boolean isHideWords() {
		return hideWords;
	}
}
