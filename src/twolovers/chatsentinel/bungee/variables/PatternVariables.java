package twolovers.chatsentinel.bungee.variables;

import net.md_5.bungee.config.Configuration;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class PatternVariables {
	final private ConfigUtil configUtil;
	final private PluginVariables pluginVariables;
	private Pattern blacklistPattern;
	private Pattern whitelistPattern;
	private Pattern namesPattern;
	private List<String> swearingCommands;

	PatternVariables(final ConfigUtil configUtil, final PluginVariables pluginVariables) {
		this.configUtil = configUtil;
		this.pluginVariables = pluginVariables;
	}

	final public void loadData() {
		final Configuration configYml = configUtil.getConfig("config.yml");
		final Configuration blacklistYml = configUtil.getConfig("blacklist.yml");
		final Configuration whitelistYml = configUtil.getConfig("whitelist.yml");

		if (blacklistYml != null)
			blacklistPattern = createPatternFromStringList(blacklistYml.getStringList("expressions"));

		if (whitelistYml != null)
			whitelistPattern = createPatternFromStringList(whitelistYml.getStringList("expressions"));

		if (configYml != null)
			swearingCommands = configYml.getStringList("swearing.commands");

		reloadNamesPattern();
	}

	private Pattern createPatternFromStringList(Collection<String> list) {
		if (list != null && !list.isEmpty()) {
			String regex = "";

			for (final String string : list) {
				regex = String.format("%s(%s)|", regex, string);
			}

			return Pattern.compile("(?i)(" + regex + "$^)");
		} else {
			return Pattern.compile("(?!x)x");
		}
	}

	final public Pattern getBlacklistPattern() {
		return blacklistPattern;
	}

	final public Pattern getWhitelistPattern() {
		return whitelistPattern;
	}

	final public Pattern getNamesPattern() {
		return namesPattern;
	}

	final public void reloadNamesPattern() {
		namesPattern = createPatternFromStringList(pluginVariables.getPlayerNames());
	}

	public boolean startsWithCommand(String message) {
		message = message.replaceAll("^/([a-z])+:", "");

		if (!message.startsWith("/"))
			message = "/" + message;

		if (swearingCommands == null || swearingCommands.isEmpty())
			return true;
		else
			for (final String command : swearingCommands)
				if (message.startsWith(command))
					return true;

		return false;
	}
}
