package dev._2lstudios.chatsentinel.bukkit.modules;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import dev._2lstudios.chatsentinel.bukkit.utils.ConfigUtil;
import dev._2lstudios.chatsentinel.shared.modules.ModuleManager;

public class BukkitModuleManager extends ModuleManager {
	private ConfigUtil configUtil;

	public BukkitModuleManager(ConfigUtil configUtil) {
		super();
		this.configUtil = configUtil;
		reloadData();
	}

	@Override
	public void reloadData() {
		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/whitelist.yml");
		configUtil.create("%datafolder%/blacklist.yml");

		Configuration blacklistYml = configUtil.get("%datafolder%/blacklist.yml");
		Configuration configYml = configUtil.get("%datafolder%/config.yml");
		Configuration messagesYml = configUtil.get("%datafolder%/messages.yml");
		Configuration whitelistYml = configUtil.get("%datafolder%/whitelist.yml");
		Map<String, Map<String, String>> locales = new HashMap<>();

		for (String lang : messagesYml.getConfigurationSection("langs").getKeys(false)) {
			ConfigurationSection langSection = messagesYml.getConfigurationSection("langs." + lang);
			Map<String, String> messages = new HashMap<>();

			for (String key : langSection.getKeys(false)) {
				String value = langSection.getString(key);

				messages.put(key, value);
			}

			locales.put(lang, messages);
		}

		getCapsModule().loadData(configYml.getBoolean("caps.enabled"), configYml.getBoolean("caps.replace"),
				configYml.getInt("caps.max"), configYml.getInt("caps.warn.max"),
				configYml.getString("caps.warn.notification"),
				configYml.getStringList("caps.punishments").toArray(new String[0]));
		getCooldownModule().loadData(configYml.getBoolean("cooldown.enabled"),
				configYml.getInt("cooldown.time.repeat-global"), configYml.getInt("cooldown.time.repeat"),
				configYml.getInt("cooldown.time.normal"), configYml.getInt("cooldown.time.command"));
		getFloodModule().loadData(configYml.getBoolean("flood.enabled"), configYml.getBoolean("flood.replace"),
				configYml.getInt("flood.warn.max"), configYml.getString("flood.pattern"),
				configYml.getString("flood.warn.notification"),
				configYml.getStringList("flood.punishments").toArray(new String[0]));
		getMessagesModule().loadData(messagesYml.getString("default"), locales);
		getGeneralModule().loadData(configYml.getBoolean("general.sanitize", true),
				configYml.getBoolean("general.sanitize-names", true),
				configYml.getBoolean("general.filter-other", false),
				configYml.getStringList("general.commands"));
		getWhitelistModule().loadData(configYml.getBoolean("whitelist.enabled"),
				whitelistYml.getStringList("expressions").toArray(new String[0]));
		getBlacklistModule().loadData(configYml.getBoolean("blacklist.enabled"),
				configYml.getBoolean("blacklist.fake_message"), configYml.getBoolean("blacklist.hide_words"),
				configYml.getInt("blacklist.warn.max"), configYml.getString("blacklist.warn.notification"),
				configYml.getStringList("blacklist.punishments").toArray(new String[0]),
				blacklistYml.getStringList("expressions").toArray(new String[0]), 
				configYml.getBoolean("blacklist.block_raw_message"));
		getSyntaxModule().loadData(configYml.getBoolean("syntax.enabled"), configYml.getInt("syntax.warn.max"),
				configYml.getString("syntax.warn.notification"),
				configYml.getStringList("syntax.whitelist").toArray(new String[0]),
				configYml.getStringList("syntax.punisments").toArray(new String[0]));
	}
}
