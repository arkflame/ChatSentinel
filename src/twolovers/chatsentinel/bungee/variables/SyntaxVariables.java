package twolovers.chatsentinel.bungee.variables;

import net.md_5.bungee.config.Configuration;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;

import java.util.regex.Pattern;

public class SyntaxVariables {
	final private ConfigUtil configUtil;
	private boolean syntaxEnabled;
	private String syntaxWarnMessage;
	private Pattern syntaxPattern;

	SyntaxVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;

		loadData();
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");

		assert configYml != null;
		syntaxEnabled = configYml.getBoolean("syntax.enabled");
		syntaxPattern = Pattern.compile("^(/)(\\w){1,}(:)(\\w){1,}");
		syntaxWarnMessage = configYml.getString("syntax.warn_message").replace("&", "§");
	}

	final public boolean isSyntaxEnabled() {
		return syntaxEnabled;
	}

	final public Pattern getSyntaxPattern() {
		return syntaxPattern;
	}

	final public String getSyntaxWarnMessage() {
		return syntaxWarnMessage;
	}
}
