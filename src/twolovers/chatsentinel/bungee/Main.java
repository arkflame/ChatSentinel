package twolovers.chatsentinel.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import twolovers.chatsentinel.bungee.commands.MainCommands;
import twolovers.chatsentinel.bungee.listeners.ChatListener;
import twolovers.chatsentinel.bungee.listeners.PlayerDisconnectListener;
import twolovers.chatsentinel.bungee.listeners.PostLoginListener;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;
import twolovers.chatsentinel.bungee.variables.PluginVariables;

public class Main extends Plugin {

	public void onEnable() {
		final ConfigUtil configUtil = new ConfigUtil(this);
		final PluginVariables pluginVariables = new PluginVariables(configUtil);
		final PluginManager pluginManager = getProxy().getPluginManager();

		configUtil.createFile("config.yml");
		configUtil.createFile("messages.yml");
		configUtil.createFile("whitelist.yml");
		configUtil.createFile("blacklist.yml");

		pluginManager.registerListener(this, new ChatListener(pluginVariables));
		pluginManager.registerListener(this, new PostLoginListener(pluginVariables));
		pluginManager.registerListener(this, new PlayerDisconnectListener(pluginVariables));

		pluginManager.registerCommand(this, new MainCommands(pluginVariables));
	}
}