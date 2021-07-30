package dev._2lstudios.chatsentinel.shared.modules;

import java.util.HashMap;
import java.util.Map;

import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class MessagesModule {
	private Map<String, Map<String, String>> locales;
	private String defaultLang = "en";

	public final void loadData(final String defaultLang, final Map<String, Map<String, String>> messages) {
		this.locales = messages;
		this.defaultLang = defaultLang;
	}

	private final String getString(final String lang, final String path) {
		final Map<String, String> messages = locales.getOrDefault(lang, locales.getOrDefault(defaultLang, locales.getOrDefault("en", new HashMap<>())));

		return messages.getOrDefault(path, "<CHATSENTINEL STRING NOT FOUND>");
	}

	public final String getCleared(final String[][] placeholders, final String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "cleared"), placeholders);
	}

	public final String getReload(final String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "reload"), null);
	}

	public final String getHelp(final String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "help"), null);
	}

	public final String getUnknownCommand(final String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "unknown_command"), null);
	}

	public final String getNoPermission(final String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "no_permission"), null);
	}

	public final String getWarnMessage(final String[][] placeholders, final String lang, final String module) {
		final String moduleLowerCase = module.toLowerCase();

		return PlaceholderUtil.replacePlaceholders(getString(lang, moduleLowerCase + "_warn_message"), placeholders);
	}
}