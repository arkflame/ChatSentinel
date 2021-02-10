package twolovers.chatsentinel.bungee.modules;

import java.util.*;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import twolovers.chatsentinel.bukkit.utils.RegexTester;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;
import twolovers.chatsentinel.shared.interfaces.Module;
import twolovers.chatsentinel.shared.modules.CapsModule;
import twolovers.chatsentinel.shared.modules.CooldownModule;
import twolovers.chatsentinel.shared.modules.BlacklistModule;
import twolovers.chatsentinel.shared.modules.FloodModule;
import twolovers.chatsentinel.shared.modules.MessagesModule;
import twolovers.chatsentinel.shared.modules.SyntaxModule;
import twolovers.chatsentinel.shared.modules.WhitelistModule;

public class ModuleManager {
	private final ProxyServer server;
	private final ConfigUtil configUtil;
	private final RegexTester regexTester;
	private final Module[] modules;
	private final CapsModule capsModule;
	private final CooldownModule cooldownModule;
	private final FloodModule floodModule;
	private final MessagesModule messagesModule;
	private final WhitelistModule whitelistModule;
	private final BlacklistModule blacklistModule;
	private final SyntaxModule syntaxModule;

	public ModuleManager(final ProxyServer server, final ConfigUtil configUtil, final RegexTester regexTester) {
		this.server = server;
		this.configUtil = configUtil;
		this.regexTester = regexTester;
		this.modules = new Module[5];
		this.modules[0] = this.capsModule = new CapsModule();
		this.modules[1] = this.cooldownModule = new CooldownModule();
		this.modules[2] = this.floodModule = new FloodModule();
		this.modules[3] = this.blacklistModule = new BlacklistModule();
		this.modules[4] = this.syntaxModule = new SyntaxModule();
		this.messagesModule = new MessagesModule();
		this.whitelistModule = new WhitelistModule();

		reloadData();
	}

	public final Module[] getModules() {
		return modules;
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

	public final WhitelistModule getWhitelistModule() {
		return whitelistModule;
	}

	public final void reloadData() {
		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/whitelist.yml");
		configUtil.create("%datafolder%/blacklist.yml");

		final Configuration blacklistYml = configUtil.get("%datafolder%/blacklist.yml");
		final Configuration configYml = configUtil.get("%datafolder%/config.yml");
		final Configuration messagesYml = configUtil.get("%datafolder%/messages.yml");
		final Configuration whitelistYml = configUtil.get("%datafolder%/whitelist.yml");
		final Map<String, Map<String, String>> locales = new HashMap<>();
		final Collection<String> playerNames = new HashSet<>();
		final String[] blackListExpressions = blacklistYml.getStringList("expressions").toArray(new String[0]);
		ArrayList<String> blackListExpressionsAccepted = new ArrayList<>();

		for (String expression : blackListExpressions) {
			if (regexTester.test(expression)) {
				blackListExpressionsAccepted.add(expression);
			} else {
				System.out.println("[ChatSentinel] Expression "+expression+" was not accepted by the regex parser");
			}
		}
		for (final ProxiedPlayer player : server.getPlayers()) {
			playerNames.add(player.getName());
		}

		for (final String lang : messagesYml.getSection("langs").getKeys()) {
			final Configuration langSection = messagesYml.getSection("langs." + lang);
			final Map<String, String> messages = new HashMap<>();

			for (final String key : langSection.getKeys()) {
				final String value = langSection.getString(key);

				messages.put(key, value);
			}

			locales.put(lang, messages);
		}

		this.capsModule.loadData(configYml.getBoolean("caps.enabled"), configYml.getBoolean("caps.replace"),
				configYml.getInt("caps.max"), configYml.getInt("caps.warn.max"),
				configYml.getString("caps.warn.notification"),
				configYml.getStringList("caps.punishments").toArray(new String[0]));
		this.cooldownModule.loadData(configYml.getBoolean("cooldown.enabled"), configYml.getInt("cooldown.time.repeat"),
				configYml.getInt("cooldown.time.normal"), configYml.getInt("cooldown.time.command"));
		this.floodModule.loadData(configYml.getBoolean("flood.enabled"), configYml.getBoolean("flood.replace"),
				configYml.getInt("flood.warn.max"), configYml.getString("flood.pattern"),
				configYml.getString("flood.warn.notification"),
				configYml.getStringList("flood.punishments").toArray(new String[0]));
		this.messagesModule.loadData(messagesYml.getString("default"), locales);
		this.whitelistModule.loadData(whitelistYml.getStringList("expressions"),
				configYml.getStringList("whitelist.commands"), configYml.getBoolean("whitelist.enabled"),
				configYml.getBoolean("whitelist.names"), playerNames);
		this.blacklistModule.loadData(configYml.getBoolean("blacklist.enabled"),
				configYml.getBoolean("blacklist.fake_message"), configYml.getBoolean("blacklist.hide_words"),
				configYml.getInt("blacklist.warn.max"), configYml.getString("blacklist.warn.notification"),
				configYml.getStringList("blacklist.punishments").toArray(new String[0]),
				blackListExpressionsAccepted);
		this.syntaxModule.loadData(configYml.getBoolean("syntax.enabled"), configYml.getInt("syntax.warn.max"),
				configYml.getString("syntax.warn.notification"),
				configYml.getStringList("syntax.whitelist").toArray(new String[0]),
				configYml.getStringList("syntax.punisments").toArray(new String[0]));
	}
}
