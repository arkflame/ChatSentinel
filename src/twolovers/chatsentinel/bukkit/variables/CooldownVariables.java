package twolovers.chatsentinel.bukkit.variables;

import org.bukkit.configuration.Configuration;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;

public class CooldownVariables {
	final private ConfigUtil configUtil;
	private boolean throttleEnabled;
	private String throttleWarnMessage;
	private long throttleTime;

	public CooldownVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");

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

	final public Long getThrottleTime() {
		return throttleTime;
	}
}
