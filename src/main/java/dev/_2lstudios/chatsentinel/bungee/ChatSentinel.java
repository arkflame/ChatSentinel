package dev._2lstudios.chatsentinel.bungee;

import dev._2lstudios.chatsentinel.bungee.commands.ChatSentinelCommand;
import dev._2lstudios.chatsentinel.bungee.listeners.ChatListener;
import dev._2lstudios.chatsentinel.bungee.modules.BungeeModuleManager;
import dev._2lstudios.chatsentinel.bungee.utils.ConfigUtil;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class ChatSentinel extends Plugin {
	@Override
	public void onEnable() {
		final ConfigUtil configUtil = new ConfigUtil(this);

		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/whitelist.yml");
		configUtil.create("%datafolder%/blacklist.yml");

		final ProxyServer server = getProxy();
		final BungeeModuleManager moduleManager = new BungeeModuleManager(configUtil);
		final ChatPlayerManager chatPlayerManager = new ChatPlayerManager();
		final PluginManager pluginManager = server.getPluginManager();

		pluginManager.registerListener(this, new ChatListener(this, moduleManager, chatPlayerManager));
		pluginManager.registerCommand(this, new ChatSentinelCommand(moduleManager, server));
	}
}