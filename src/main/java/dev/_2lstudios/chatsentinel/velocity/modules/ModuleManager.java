package dev._2lstudios.chatsentinel.velocity.modules;

import com.google.common.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;
import dev._2lstudios.chatsentinel.shared.modules.*;
import dev._2lstudios.chatsentinel.velocity.utils.ConfigUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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

		final ConfigurationNode blacklistYml = configUtil.get("%datafolder%/blacklist.yml");
		final ConfigurationNode configYml = configUtil.get("%datafolder%/config.yml");
		final ConfigurationNode messagesYml = configUtil.get("%datafolder%/messages.yml");
		final ConfigurationNode whitelistYml = configUtil.get("%datafolder%/whitelist.yml");
		final Map<String, Map<String, String>> locales = new HashMap<>();
		final Collection<String> playerNames = new HashSet<>();

		for (final Player player : server.getAllPlayers()) {
			playerNames.add(player.getUsername());
		}

		try {
			for (final String lang : messagesYml.getNode("langs").getChildrenMap().keySet().toArray(new String[0])) {
				final ConfigurationNode langSection = messagesYml.getNode("langs", lang);
				final Map<String, String> messages = new HashMap<>();

				for (final String key : langSection.getChildrenMap().keySet().toArray(new String[0])) {
					final String value = langSection.getNode(key).getString();
					messages.put(key, value);
				}

				locales.put(lang, messages);
			}

			this.capsModule.loadData(configYml.getNode("caps", "enabled").getBoolean(), configYml.getNode("caps", "replace").getBoolean(),
					configYml.getNode("caps", "max").getInt(), configYml.getNode("caps", "warn", "max").getInt(),
					configYml.getNode("caps", "warn", "notification").getString(),
					configYml.getNode("caps", "punishments").getList(new TypeToken<String>(){}).toArray(new String[0]));
			this.cooldownModule.loadData(configYml.getNode("cooldown", "enabled").getBoolean(), configYml.getNode("cooldown", "time", "repeat-global").getInt(),
					configYml.getNode("cooldown", "time", "repeat").getInt(), configYml.getNode("cooldown", "time", "normal").getInt(),
					configYml.getNode("cooldown", "time", "command").getInt());
			this.floodModule.loadData(configYml.getNode("flood", "enabled").getBoolean(), configYml.getNode("flood", "replace").getBoolean(),
					configYml.getNode("flood", "warn", "max").getInt(), configYml.getNode("flood", "pattern").getString(),
					configYml.getNode("flood", "warn", "notification").getString(),
					configYml.getNode("flood", "punishments").getList(new TypeToken<String>(){}).toArray(new String[0]));
			this.messagesModule.loadData(messagesYml.getNode("default").getString(), locales);
			this.generalModule.loadData(configYml.getNode("general", "commands").getList(new TypeToken<String>(){}));
			this.whitelistModule.loadData(configYml.getNode("whitelist", "enabled").getBoolean(),
					whitelistYml.getNode("expressions").getList(new TypeToken<String>(){}).toArray(new String[0]));
			this.blacklistModule.loadData(configYml.getNode("blacklist", "enabled").getBoolean(),
					configYml.getNode("blacklist", "fake_message").getBoolean(), configYml.getNode("blacklist", "hide_words").getBoolean(),
					configYml.getNode("blacklist", "warn", "max").getInt(), configYml.getNode("blacklist", "warn", "notification").getString(),
					configYml.getNode("blacklist", "punishments").getList(new TypeToken<String>(){}).toArray(new String[0]),
					blacklistYml.getNode("expressions").getList(new TypeToken<String>(){}).toArray(new String[0]));
			this.syntaxModule.loadData(configYml.getNode("syntax", "enabled").getBoolean(), configYml.getNode("syntax", "warn", "max").getInt(),
					configYml.getNode("syntax", "warn", "notification").getString(),
					configYml.getNode("syntax", "whitelist").getList(new TypeToken<String>(){}).toArray(new String[0]),
					configYml.getNode("syntax", "punisments").getList(new TypeToken<String>(){}).toArray(new String[0]));
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
	}
}
