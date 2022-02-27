package dev._2lstudios.chatsentinel.velocity.modules;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.modules.*;
import dev._2lstudios.chatsentinel.velocity.utils.ConfigUtil;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ModuleManager {
	private final ProxyServer server;
	private final ConfigUtil configUtil;
	private final Module[] modules;
	private final CapsModule capsModule;
	private final CooldownModule cooldownModule;
	private final FloodModule floodModule;
	private final MessagesModule messagesModule;
	private final GeneralModule generalModule;
	private final BlacklistModule blacklistModule;
	private final SyntaxModule syntaxModule;
	private final WhitelistModule whitelistModule;

	public ModuleManager(final ProxyServer server, final ConfigUtil configUtil) {
		this.server = server;
		this.configUtil = configUtil;
		this.modules = new Module[5];
		this.modules[0] = this.capsModule = new CapsModule();
		this.modules[1] = this.cooldownModule = new CooldownModule();
		this.modules[2] = this.floodModule = new FloodModule();
		this.modules[3] = this.blacklistModule = new BlacklistModule();
		this.modules[4] = this.syntaxModule = new SyntaxModule();
		this.messagesModule = new MessagesModule();
		this.generalModule = new GeneralModule();
		this.whitelistModule = new WhitelistModule();

		reloadData();
	}

	public final Module[] getModules() {
		return modules;
	}

    public CooldownModule getCooldownModule() {
        return cooldownModule;
    }

	public final FloodModule getFloodModule() {
		return floodModule;
	}

	public final BlacklistModule getBlacklistModule() {
		return blacklistModule;
	}

	public final SyntaxModule getSyntaxModule() {
		return syntaxModule;
	}

	public final MessagesModule getMessagesModule() {
		return messagesModule;
	}

	public final GeneralModule getGeneralModule() {
		return generalModule;
	}

	public final WhitelistModule getWhitelistModule() {
		return whitelistModule;
	}

	public final void reloadData() {
		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/blacklist.yml");
		configUtil.create("%datafolder%/whitelist.yml");

		final YamlFile blacklistYml = configUtil.get("%datafolder%/blacklist.yml");
		final YamlFile configYml = configUtil.get("%datafolder%/config.yml");
		final YamlFile messagesYml = configUtil.get("%datafolder%/messages.yml");
		final YamlFile whitelistYml = configUtil.get("%datafolder%/whitelist.yml");
		final Map<String, Map<String, String>> locales = new HashMap<>();
		final Collection<String> playerNames = new HashSet<>();

		for (final Player player : server.getAllPlayers()) {
			playerNames.add(player.getUsername());
		}

		for (final String lang : messagesYml.getConfigurationSection("langs").getKeys(false)) {
			final ConfigurationSection langSection = messagesYml.getConfigurationSection("langs." + lang);
			final Map<String, String> messages = new HashMap<>();

			for (final String key : langSection.getKeys(false)) {
				final String value = langSection.getString(key);

				messages.put(key, value);
			}

			locales.put(lang, messages);
		}

		this.capsModule.loadData(configYml.getBoolean("caps.enabled"), configYml.getBoolean("caps.replace"),
				configYml.getInt("caps.max"), configYml.getInt("caps.warn.max"),
				configYml.getString("caps.warn.notification"),
				configYml.getStringList("caps.punishments").toArray(new String[0]));
		this.cooldownModule.loadData(configYml.getBoolean("cooldown.enabled"), configYml.getInt("cooldown.time.repeat-global"), configYml.getInt("cooldown.time.repeat"),
				configYml.getInt("cooldown.time.normal"), configYml.getInt("cooldown.time.command"));
		this.floodModule.loadData(configYml.getBoolean("flood.enabled"), configYml.getBoolean("flood.replace"),
				configYml.getInt("flood.warn.max"), configYml.getString("flood.pattern"),
				configYml.getString("flood.warn.notification"),
				configYml.getStringList("flood.punishments").toArray(new String[0]));
		this.messagesModule.loadData(messagesYml.getString("default"), locales);
		this.generalModule.loadData(configYml.getStringList("general.commands"));
		this.whitelistModule.loadData(configYml.getBoolean("whitelist.enabled"), whitelistYml.getStringList("expressions").toArray(new String[0]));
		this.blacklistModule.loadData(configYml.getBoolean("blacklist.enabled"),
				configYml.getBoolean("blacklist.fake_message"), configYml.getBoolean("blacklist.hide_words"),
				configYml.getInt("blacklist.warn.max"), configYml.getString("blacklist.warn.notification"),
				configYml.getStringList("blacklist.punishments").toArray(new String[0]),
				blacklistYml.getStringList("expressions").toArray(new String[0]));
		this.syntaxModule.loadData(configYml.getBoolean("syntax.enabled"), configYml.getInt("syntax.warn.max"),
				configYml.getString("syntax.warn.notification"),
				configYml.getStringList("syntax.whitelist").toArray(new String[0]),
				configYml.getStringList("syntax.punisments").toArray(new String[0]));
	}
}
