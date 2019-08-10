package twolovers.chatsentinel.bungee.variables;

import net.md_5.bungee.config.Configuration;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;

public class ThrottleVariables {
	final private ConfigUtil configUtil;
	private boolean throttleEnabled;
	private String throttleWarnMessage;
	private long throttleTime;

	ThrottleVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");

		assert configYml != null;
		throttleEnabled = configYml.getBoolean("cooldown.enabled");
		throttleWarnMessage = configYml.getString("cooldown.warn_message").replace("&", "§");
		throttleTime = configYml.getLong("cooldown.time");
	}

	final public boolean isEnabled() {
		return throttleEnabled;
	}

	final public String getWarnMessage() {
		return throttleWarnMessage;
	}

	final public Long getTime() {
		return throttleTime;
	}
}
