package twolovers.chatsentinel.bukkit;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import twolovers.chatsentinel.bukkit.commands.ChatSentinelCommand;
import twolovers.chatsentinel.bukkit.listeners.AsyncPlayerChatListener;
import twolovers.chatsentinel.bukkit.listeners.PlayerJoinListener;
import twolovers.chatsentinel.bukkit.listeners.PlayerQuitListener;
import twolovers.chatsentinel.bukkit.listeners.ServerCommandListener;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;
import twolovers.chatsentinel.bukkit.utils.VersionUtil;
import twolovers.chatsentinel.shared.chat.ChatPlayerManager;
import twolovers.chatsentinel.shared.modules.WhitelistModule;
import twolovers.chatsentinel.bukkit.modules.ModuleManager;

public class ChatSentinel extends JavaPlugin {

	@Override
	public void onEnable() {
		final ConfigUtil configUtil = new ConfigUtil(this);
		final Server server = this.getServer();

		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/whitelist.yml");
		configUtil.create("%datafolder%/blacklist.yml");

		VersionUtil.start(server.getVersion());

		final ModuleManager moduleManager = new ModuleManager(configUtil);
		final ChatPlayerManager chatPlayerManager = new ChatPlayerManager();
		final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
		final PluginManager pluginManager = server.getPluginManager();

		pluginManager.registerEvents(new AsyncPlayerChatListener(this, moduleManager, chatPlayerManager), this);
		pluginManager.registerEvents(new PlayerJoinListener(moduleManager, chatPlayerManager), this);
		pluginManager.registerEvents(new PlayerQuitListener(moduleManager, chatPlayerManager), this);
		pluginManager.registerEvents(new ServerCommandListener(this, moduleManager, chatPlayerManager), this);

		getCommand("chatsentinel").setExecutor(new ChatSentinelCommand(moduleManager));

		server.getScheduler().runTaskTimerAsynchronously(this, () -> {
			chatPlayerManager.clear();
			whitelistModule.reloadPattern();
		}, 200L, 200L);
	}

	@Override
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);
	}

	public long capsCount(final String string) {
		return string.codePoints().filter(c -> c >= 'A' && c <= 'Z').count();
	}
}