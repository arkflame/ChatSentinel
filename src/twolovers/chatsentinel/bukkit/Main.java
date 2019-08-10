package twolovers.chatsentinel.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import twolovers.chatsentinel.bukkit.commands.MainCommand;
import twolovers.chatsentinel.bukkit.listeners.AsyncPlayerChatListener;
import twolovers.chatsentinel.bukkit.listeners.PlayerJoinListener;
import twolovers.chatsentinel.bukkit.listeners.PlayerQuitListener;
import twolovers.chatsentinel.bukkit.listeners.ServerCommandListener;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;
import twolovers.chatsentinel.bukkit.variables.PluginVariables;

public class Main extends JavaPlugin {

	public void onEnable() {
		final ConfigUtil configUtil = new ConfigUtil(this);
		final PluginVariables pluginVariables = new PluginVariables(configUtil);
		final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

		configUtil.createFile("config.yml");
		configUtil.createFile("messages.yml");
		configUtil.createFile("whitelist.yml");
		configUtil.createFile("blacklist.yml");

		pluginManager.registerEvents(new AsyncPlayerChatListener(this, pluginVariables), this);
		pluginManager.registerEvents(new PlayerJoinListener(pluginVariables), this);
		pluginManager.registerEvents(new PlayerQuitListener(pluginVariables), this);
		pluginManager.registerEvents(new ServerCommandListener(this, pluginVariables), this);

		getCommand("chatsentinel").setExecutor(new MainCommand(pluginVariables));
	}
}