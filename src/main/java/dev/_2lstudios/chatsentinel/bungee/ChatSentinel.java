package dev._2lstudios.chatsentinel.bungee;

import java.util.concurrent.TimeUnit;

import dev._2lstudios.chatsentinel.bungee.commands.ChatSentinelCommand;
import dev._2lstudios.chatsentinel.bungee.listeners.ChatListener;
import dev._2lstudios.chatsentinel.bungee.listeners.PlayerDisconnectListener;
import dev._2lstudios.chatsentinel.bungee.listeners.PostLoginListener;
import dev._2lstudios.chatsentinel.bungee.modules.ModuleManager;
import dev._2lstudios.chatsentinel.bungee.utils.ConfigUtil;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class ChatSentinel extends Plugin {

	public void onEnable() {
		final ConfigUtil configUtil = new ConfigUtil(this);

		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/whitelist.yml");
		configUtil.create("%datafolder%/blacklist.yml");

		final ProxyServer server = getProxy();
		final ModuleManager moduleManager = new ModuleManager(server, configUtil);
		final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
		final ChatPlayerManager chatPlayerManager = new ChatPlayerManager();
		final PluginManager pluginManager = server.getPluginManager();

		pluginManager.registerListener(this, new ChatListener(this, moduleManager, chatPlayerManager));
		pluginManager.registerListener(this, new PostLoginListener(moduleManager, chatPlayerManager));
		pluginManager.registerListener(this, new PlayerDisconnectListener(moduleManager, chatPlayerManager));
		pluginManager.registerCommand(this, new ChatSentinelCommand(moduleManager, server));

		server.getScheduler().schedule(this, () -> {
			chatPlayerManager.clear();
			whitelistModule.reloadNamesPattern();
		}, 10, 10, TimeUnit.SECONDS);
	}
}