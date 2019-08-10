package twolovers.chatsentinel.bukkit.variables;

import org.bukkit.configuration.Configuration;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;

public class MessagesVariables {
	final private ConfigUtil configUtil;
	private String reload;
	private String usage;
	private String unknownCommand;
	private String noPermission;

	public MessagesVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		try {
			final Configuration messages = configUtil.getConfig("messages.yml");

			if (messages != null) {
				reload = messages.getString("reload").replace("&", "\u00A7");
				usage = messages.getString("help").replace("&", "\u00A7");
				unknownCommand = messages.getString("unknowncommand").replace("&", "\u00A7");
				noPermission = messages.getString("nopermission").replace("&", "\u00A7");
			}
		} catch (NullPointerException e) {
			System.out.println("[ExploitFixer] Your Messages configuration is wrong, please check your configuration.");
		}
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