package dev._2lstudios.chatsentinel.shared.modules;

import java.util.HashMap;
import java.util.Map;

import dev._2lstudios.chatsentinel.shared.utils.PlaceholderUtil;

public class MessagesModule {
	private Map<String, Map<String, String>> locales;
	private String defaultLang = "en";

	public void loadData(String defaultLang, Map<String, Map<String, String>> messages) {
		this.locales = messages;
		this.defaultLang = defaultLang;
	}

	private String getString(String lang, String path) {
		Map<String, String> messages = locales.getOrDefault(lang, locales.getOrDefault(defaultLang, locales.getOrDefault("en", new HashMap<>())));

		return messages.getOrDefault(path, "<CHATSENTINEL STRING NOT FOUND>");
	}

	public String getCleared(String[][] placeholders, String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "cleared"), placeholders);
	}

	public String getReload(String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "reload"));
	}

	public String getHelp(String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "help"));
	}

	public String getUnknownCommand(String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "unknown_command"));
	}

	public String getNoPermission(String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "no_permission"));
	}

	public String getWarnMessage(String[][] placeholders, String lang, String module) {
		String moduleLowerCase = module.toLowerCase();

		return PlaceholderUtil.replacePlaceholders(getString(lang, moduleLowerCase + "_warn_message"), placeholders);
	}

	public String getFiltered(String lang) {
		return PlaceholderUtil.replacePlaceholders(getString(lang, "filtered"));
	}
}