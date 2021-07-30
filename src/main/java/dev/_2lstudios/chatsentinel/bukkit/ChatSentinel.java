package dev._2lstudios.chatsentinel.bukkit;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev._2lstudios.chatsentinel.bukkit.commands.ChatSentinelCommand;
import dev._2lstudios.chatsentinel.bukkit.listeners.AsyncPlayerChatListener;
import dev._2lstudios.chatsentinel.bukkit.listeners.PlayerJoinListener;
import dev._2lstudios.chatsentinel.bukkit.listeners.PlayerQuitListener;
import dev._2lstudios.chatsentinel.bukkit.listeners.ServerCommandListener;
import dev._2lstudios.chatsentinel.bukkit.modules.ModuleManager;
import dev._2lstudios.chatsentinel.bukkit.utils.ConfigUtil;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;

public class ChatSentinel extends JavaPlugin {

	@Override
	public void onEnable() {
		final ConfigUtil configUtil = new ConfigUtil(this);
		final Server server = getServer();

		final ModuleManager moduleManager = new ModuleManager(server, configUtil);
		final ChatPlayerManager chatPlayerManager = new ChatPlayerManager();
		final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
		final PluginManager pluginManager = server.getPluginManager();

		pluginManager.registerEvents(new AsyncPlayerChatListener(this, moduleManager, chatPlayerManager), this);
		pluginManager.registerEvents(new PlayerJoinListener(moduleManager, chatPlayerManager), this);
		pluginManager.registerEvents(new PlayerQuitListener(moduleManager, chatPlayerManager), this);
		pluginManager.registerEvents(new ServerCommandListener(this, moduleManager, chatPlayerManager), this);

		getCommand("chatsentinel").setExecutor(new ChatSentinelCommand(moduleManager, server));

		server.getScheduler().runTaskTimerAsynchronously(this, () -> {
			chatPlayerManager.clear();
			whitelistModule.reloadNamesPattern();
		}, 200L, 200L);
	}

	@Override
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);
	}
}