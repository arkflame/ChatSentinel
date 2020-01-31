package twolovers.chatsentinel.bungee.modules;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
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
	private final ConfigUtil configUtil;
	private final Module[] modules;
	private final CapsModule capsModule;
	private final CooldownModule cooldownModule;
	private final FloodModule floodModule;
	private final MessagesModule messagesModule;
	private final WhitelistModule whitelistModule;
	private final BlacklistModule blacklistModule;
	private final SyntaxModule syntaxModule;

	public ModuleManager(final ConfigUtil configUtil) {
		this.configUtil = configUtil;
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

	public Module[] getModules() {
		return modules;
	}

	final public FloodModule getFloodModule() {
		return floodModule;
	}

	final public BlacklistModule getBlacklistModule() {
		return blacklistModule;
	}

	final public SyntaxModule getSyntaxModule() {
		return syntaxModule;
	}

	final public MessagesModule getMessagesModule() {
		return messagesModule;
	}

	final public WhitelistModule getWhitelistModule() {
		return whitelistModule;
	}

	final public void reloadData() {
		final Configuration blacklistYml = configUtil.get("%datafolder%/blacklist.yml"),
				configYml = configUtil.get("%datafolder%/config.yml"),
				messagesYml = configUtil.get("%datafolder%/messages.yml"),
				whitelistYml = configUtil.get("%datafolder%/whitelist.yml");
		final Collection<String> playerNames = new HashSet<>(), langs = messagesYml.getSection("langs").getKeys();
		final String[][] messageList = new String[langs.size()][9];
		int i = 0;

		for (final ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
			playerNames.add(player.getName());

		for (final String lang : langs) {
			final Configuration langSection = messagesYml.getSection("langs." + lang);
			final String[] langMessages = new String[10];

			langMessages[0] = lang;
			langMessages[1] = langSection.getString("reload");
			langMessages[2] = langSection.getString("help");
			langMessages[3] = langSection.getString("unknowncommand");
			langMessages[4] = langSection.getString("nopermission");
			langMessages[5] = langSection.getString("blacklist_warn_message");
			langMessages[6] = langSection.getString("caps_warn_message");
			langMessages[7] = langSection.getString("cooldown_warn_message");
			langMessages[8] = langSection.getString("flood_warn_message");
			langMessages[9] = langSection.getString("syntax_warn_message");
			messageList[i] = langMessages;
			i++;
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
		this.messagesModule.loadData(messagesYml.getString("default"), messageList);
		this.whitelistModule.loadData(whitelistYml.getStringList("expressions"),
				configYml.getStringList("whitelist.commands"), configYml.getBoolean("whitelist.enabled"),
				configYml.getBoolean("whitelist.names"), playerNames);
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
