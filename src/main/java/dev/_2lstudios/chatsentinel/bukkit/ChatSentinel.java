package dev._2lstudios.chatsentinel.bukkit;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev._2lstudios.chatsentinel.bukkit.commands.ChatSentinelCommand;
import dev._2lstudios.chatsentinel.bukkit.listeners.AsyncPlayerChatListener;
import dev._2lstudios.chatsentinel.bukkit.listeners.PlayerJoinListener;
import dev._2lstudios.chatsentinel.bukkit.listeners.PlayerQuitListener;
import dev._2lstudios.chatsentinel.bukkit.listeners.ServerCommandListener;
import dev._2lstudios.chatsentinel.bukkit.modules.BukkitModuleManager;
import dev._2lstudios.chatsentinel.bukkit.utils.ConfigUtil;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;

public class ChatSentinel extends JavaPlugin {
	@Override
	public void onEnable() {
		ConfigUtil configUtil = new ConfigUtil(this);
		Server server = getServer();

		BukkitModuleManager moduleManager = new BukkitModuleManager(configUtil);
		GeneralModule generalModule = moduleManager.getGeneralModule();
		ChatPlayerManager chatPlayerManager = new ChatPlayerManager();
		PluginManager pluginManager = server.getPluginManager();

		pluginManager.registerEvents(new AsyncPlayerChatListener(this, moduleManager, chatPlayerManager), this);
		pluginManager.registerEvents(new PlayerJoinListener(generalModule, chatPlayerManager), this);
		pluginManager.registerEvents(new PlayerQuitListener(moduleManager.getGeneralModule()), this);
		pluginManager.registerEvents(new ServerCommandListener(this, moduleManager, chatPlayerManager), this);

		getCommand("chatsentinel").setExecutor(new ChatSentinelCommand(chatPlayerManager, moduleManager, server));

		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			if (generalModule.needsNicknameCompile()) {
				generalModule.compileNicknamesPattern();
			}
		}, 20L, 20L);
	}
}