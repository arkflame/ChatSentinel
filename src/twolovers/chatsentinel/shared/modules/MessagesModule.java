package twolovers.chatsentinel.shared.modules;

import twolovers.chatsentinel.shared.utils.PlaceholderUtil;

public class MessagesModule {
	private String[][] messages;
	private int defaultLangNumber = 0;

	final public void loadData(final String defaultLang, final String[][] messages) {
		this.messages = messages;
		this.defaultLangNumber = this.getLangNumber(defaultLang);
	}

	final public String getReload(final String lang) {
		return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][1], null);
	}

	final public String getHelp(final String lang) {
		return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][2], null);
	}

	final public String getUnknownCommand(final String lang) {
		return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][3], null);
	}

	final public String getNoPermission(final String lang) {
		return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][4], null);
	}

	final public String getWarnMessage(final String[][] placeholders, final String lang, final String module) {
		if (module.equals("Blacklist"))
			return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][5], placeholders);
		else if (module.equals("Caps"))
			return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][6], placeholders);
		else if (module.equals("Cooldown"))
			return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][7], placeholders);
		else if (module.equals("Flood"))
			return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][8], placeholders);
		else if (module.equals("Syntax"))
			return PlaceholderUtil.replacePlaceholders(messages[getLangNumber(lang)][9], placeholders);
		else
			return "";
	}

	final private int getLangNumber(final String lang) {
		int langNumber = defaultLangNumber;

		if (lang != null && !lang.isEmpty()) {
			for (int i = 0; i < messages.length; i++) {
				if (lang.startsWith(messages[i][0])) {
					langNumber = i;
					break;
				}
			}
		}

		return langNumber;
	}

	public String getDefault() {
		return null;
	}
}