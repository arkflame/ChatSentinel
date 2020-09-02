package twolovers.chatsentinel.shared.modules;

import java.text.Normalizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class WhitelistModule {
	private Pattern pattern = null, namesPattern = null;
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
		this.playerNamesChanged = true;
		this.commands = commands;
		this.playerNames = playerNames;

		reloadNamesPattern();
	}

	final public boolean isEnabled() {
		return enabled;
	}

	final private String createPatternFromStringCollection(final Collection<String> collection) {
		if (!collection.isEmpty()) {
			final StringBuilder regex = new StringBuilder();

			for (final String string : collection) {
				regex.append("|" + string);
			}

			return "(?i)((?!x)x" + regex + ")";
		} else {
			return "(?!x)x";
		}
	}

	final public Pattern getPattern() {
		return pattern;
	}

	final public Pattern getNamesPattern() {
		return namesPattern;
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

	final public void reloadNamesPattern() {
		if (this.playerNamesChanged) {
			this.namesPattern = Pattern.compile(createPatternFromStringCollection(this.playerNames));
			this.playerNamesChanged = false;
		}
	}

	public boolean startsWithCommand(final String message) {
		for (final String command : commands) {
			if (message.toLowerCase().startsWith(command + ' '))
				return true;
		}

		return false;
	}

	public String formatMessage(String message) {
		/*
		 * Removes accents Credit: https://stackoverflow.com/users/636009/david-conrad
		 */

		final char[] out = new char[message.length()];

		message = Normalizer.normalize(message, Normalizer.Form.NFD);

		for (int j = 0, i = 0, n = message.length(); i < n; ++i) {
			final char c = message.charAt(i);

			if (c <= '\u007F') {
				out[j++] = c;
			}
		}

		return new String(out).replace("(punto)", ".").replace("(dot)", ".");
	}
}
