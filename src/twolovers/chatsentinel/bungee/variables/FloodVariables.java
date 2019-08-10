package twolovers.chatsentinel.bungee.variables;

import net.md_5.bungee.config.Configuration;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;

public class FloodVariables {
	final private ConfigUtil configUtil;
	private boolean floodEnabled;
	private long floodTime;
	private String floodWarnMessage;

	FloodVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");

		assert configYml != null;
		floodEnabled = configYml.getBoolean("flood.enabled");
		floodTime = configYml.getLong("flood.time");
		floodWarnMessage = configYml.getString("flood.warn_message").replace("&", "§");
	}

	final public boolean isEnabled() {
		return floodEnabled;
	}

	final public long getTime() {
		return floodTime;
	}

	final public String getFloodWarnMessage() {
		return floodWarnMessage;
	}
}
