package twolovers.chatsentinel.bungee.variables;

import net.md_5.bungee.config.Configuration;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;

public class MessagesVariables {
	final private ConfigUtil configUtil;
	private String reload;
	private String usage;
	private String unknownCommand;
	private String noPermission;

	MessagesVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		final Configuration messagesConfig = configUtil.getConfig("messages.yml");

		assert messagesConfig != null;
		reload = messagesConfig.getString("reload").replace("&", "\u00A7");
		usage = messagesConfig.getString("help").replace("&", "\u00A7");
		unknownCommand = messagesConfig.getString("unknowncommand").replace("&", "\u00A7");
		noPermission = messagesConfig.getString("nopermission").replace("&", "\u00A7");
	}

	final public String getReloadMessage() {
		return reload;
	}

	final public String getUsageMessage() {
		return usage;
	}

	final public String getUnknownCommand() {
		return unknownCommand;
	}

	final public String getNoPermission() {
		return noPermission;
	}
}