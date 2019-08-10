package twolovers.chatsentinel.bukkit.variables;

import org.bukkit.configuration.Configuration;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;

public class FloodVariables {
	final private ConfigUtil configUtil;
	private boolean floodEnabled;
	private long floodTime;
	private String floodWarnMessage;

	public FloodVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");

		floodEnabled = configYml.getBoolean("flood.enabled");
		floodTime = configYml.getLong("flood.time");
		floodWarnMessage = configYml.getString("flood.warn_message").replace("&", "§");
	}

	final public boolean isEnabled() {
		return floodEnabled;
	}

	final public long getFloodTime() {
		return floodTime;
	}

	final public String getFloodWarnMessage() {
		return floodWarnMessage;
	}
}
