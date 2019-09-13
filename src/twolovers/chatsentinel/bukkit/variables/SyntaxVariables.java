package twolovers.chatsentinel.bukkit.variables;

import org.bukkit.configuration.Configuration;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;

import java.util.Collection;
import java.util.regex.Pattern;

public class SyntaxVariables {
	final private ConfigUtil configUtil;
	private boolean syntaxEnabled;
	private Collection<String> syntaxWhitelist;
	private String syntaxWarnMessage;
	private Pattern syntaxPattern;

	public SyntaxVariables(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");

		syntaxEnabled = configYml.getBoolean("syntax.enabled");
		syntaxPattern = Pattern.compile("^(/)(\\w){1,}(:)(\\w){1,}");
		syntaxWhitelist = configYml.getStringList("syntax.whitelist");
		syntaxWarnMessage = configYml.getString("syntax.warn_message").replace("&", "§");
	}

	final public boolean isSyntaxEnabled() {
		return syntaxEnabled;
	}

	final public Pattern getSyntaxPattern() {
		return syntaxPattern;
	}

	final public boolean isWhitelisted(String message) {
		if (syntaxWhitelist.size() > 0)
			for (String string : syntaxWhitelist) if (message.startsWith(string)) return true;

		return false;
	}

	final public String getSyntaxWarnMessage() {
		return syntaxWarnMessage;
	}
}
