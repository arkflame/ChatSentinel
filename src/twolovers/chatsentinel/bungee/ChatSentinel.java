package twolovers.chatsentinel.bungee;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import twolovers.chatsentinel.bungee.commands.ChatSentinelCommand;
import twolovers.chatsentinel.bungee.listeners.ChatListener;
import twolovers.chatsentinel.bungee.listeners.PlayerDisconnectListener;
import twolovers.chatsentinel.bungee.listeners.PostLoginListener;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;
import twolovers.chatsentinel.shared.chat.ChatPlayerManager;
import twolovers.chatsentinel.shared.modules.WhitelistModule;
import twolovers.chatsentinel.bungee.modules.ModuleManager;

public class ChatSentinel extends Plugin {

	public void onEnable() {
		final ConfigUtil configUtil = new ConfigUtil(this);

		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/whitelist.yml");
		configUtil.create("%datafolder%/blacklist.yml");

		final ModuleManager moduleManager = new ModuleManager(configUtil);
		final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
		final ChatPlayerManager chatPlayerManager = new ChatPlayerManager();
		final ProxyServer proxy = getProxy();
		final PluginManager pluginManager = proxy.getPluginManager();

		pluginManager.registerListener(this, new ChatListener(this, moduleManager, chatPlayerManager));
		pluginManager.registerListener(this, new PostLoginListener(moduleManager, chatPlayerManager));
		pluginManager.registerListener(this, new PlayerDisconnectListener(moduleManager, chatPlayerManager));
		pluginManager.registerCommand(this, new ChatSentinelCommand(moduleManager));

		proxy.getScheduler().schedule(this, () -> {
			chatPlayerManager.clear();
			whitelistModule.reloadPattern();
		}, 10, 10, TimeUnit.SECONDS);
	}
}