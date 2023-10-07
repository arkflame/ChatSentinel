package dev._2lstudios.chatsentinel.shared.modules;

import java.text.Normalizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class GeneralModule {
	private Pattern nonAlphaNumericPattern = Pattern.compile("[^a-zA-Z0-9]");
	private Pattern nicknamesPattern = Pattern.compile("");
	private Collection<String> nicknames = new HashSet<>();
	private Collection<String> commands;
	private boolean sanitize;
	private boolean sanitizeNames;
	private boolean filterOther;

	public void loadData(boolean sanitize, boolean sanitizeNames, boolean filterOther,
			Collection<String> commands) {
		this.sanitize = sanitize;
		this.sanitizeNames = sanitizeNames;
		this.filterOther = filterOther;
		this.commands = commands;
	}

	public boolean isSanitizeEnabled() {
		return sanitize;
	}

	/*
	 * Removes non latin words Credit:
	 * https://stackoverflow.com/users/636009/david-conrad
	 */
	public String sanitize(String message) {
		char[] out = new char[message.length()];

		message = Normalizer.normalize(message, Normalizer.Form.NFD);

		for (int j = 0, i = 0, n = message.length(); i < n; ++i) {
			char c = message.charAt(i);

			if (c <= '\u007F') {
				out[j++] = c;
			}
		}

		return new String(out).replace("(punto)", ".").replace("(dot)", ".").trim();
	}

	public boolean isSanitizeNames() {
		return sanitizeNames;
	}

	public String removeNonAlphanumeric(String text) {
		return nonAlphaNumericPattern.matcher(text).replaceAll("");
	}

	private boolean needsNicknameCompile = false;

	public boolean needsNicknameCompile() {
		return needsNicknameCompile;
	}

	public void compileNicknamesPattern() {
		needsNicknameCompile = false;

		StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;

		for (String nickname : nicknames) {
			if (!first) {
				stringBuilder.append("|");
			} else {
				first = false;
			}

			stringBuilder.append("(?i)(" + nickname + ")");
		}

		nicknamesPattern = Pattern.compile(stringBuilder.toString());
	}

	public Pattern getNicknamesPattern() {
		return nicknamesPattern;
	}

	public void addNickname(String nickname) {
		// Remove alphanumeric to avoid errors
		nicknames.add(removeNonAlphanumeric(nickname));

		// Compile the pattern with the nicknames
		needsNicknameCompile = true;
	}

	public void removeNickname(String nickname) {
		// Remove alphanumeric to avoid errors
		nicknames.remove(removeNonAlphanumeric(nickname));
		
		// Compile the pattern with the nicknames
		needsNicknameCompile = true;
	}

	public String sanitizeNames(String message) {
		return nicknamesPattern.matcher(message).replaceAll("");
	}

	public boolean isCommand(String message) {
		message = message.toLowerCase();

		for (String command : commands) {
			if (message.startsWith(command + ' '))
				return true;
		}

		return false;
	}

	public boolean isFilterOther() {
		return filterOther;
	}
}
