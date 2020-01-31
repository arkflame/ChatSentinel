package twolovers.chatsentinel.shared.modules;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class WhitelistModule {
	private Pattern pattern = null;
	private Collection<String> playerNames, commands;
	private boolean enabled, names, playerNamesChanged;

	public WhitelistModule() {
		this.playerNames = new HashSet<>();
	}

	final public void loadData(final Collection<String> whitelistExpressions, final Collection<String> commands,
			final boolean enabled, final boolean names, final Collection<String> playerNames) {
		this.pattern = Pattern.compile(createPatternFromStringCollection(whitelistExpressions));
		this.enabled = enabled;
		this.names = names;
		this.playerNamesChanged = false;
		this.commands = commands;
		this.playerNames = playerNames;

		reloadPattern();
	}

	final public boolean isEnabled() {
		return enabled;
	}

	final private String createPatternFromStringCollection(final Collection<String> collection) {
		if (collection.size() > 0) {
			String regex = "";

			for (final String string : collection) {
				regex = String.format("%s(%s)|", regex, string);
			}

			return "(?i)(" + regex + "(?!x)x)";
		} else {
			return "(?!x)x";
		}
	}

	final public Pattern getPattern() {
		return pattern;
	}

	final public void addName(final String playerName) {
		if (names) {
			this.playerNames.add(playerName);
			this.playerNamesChanged = true;
		}
	}

	final public void removeName(final String playerName) {
		if (names) {
			this.playerNames.remove(playerName);
			this.playerNamesChanged = true;
		}
	}

	final public void reloadPattern() {
		if (this.playerNamesChanged) {
			this.pattern = Pattern.compile(pattern.toString() + createPatternFromStringCollection(this.playerNames));
		}
	}

	public boolean startsWithCommand(String message) {
		for (final String command : commands) {
			if (message.toLowerCase().startsWith(command + ' '))
				return true;
		}

		return false;
	}
}
