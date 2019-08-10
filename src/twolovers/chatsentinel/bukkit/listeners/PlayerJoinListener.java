package twolovers.chatsentinel.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import twolovers.chatsentinel.bukkit.variables.PatternVariables;
import twolovers.chatsentinel.bukkit.variables.PluginVariables;

public class PlayerJoinListener implements Listener {
	final private PluginVariables pluginVariables;

	public PlayerJoinListener(final PluginVariables pluginVariables) {
		this.pluginVariables = pluginVariables;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final PatternVariables patternVariables = pluginVariables.getPatternVariables();

		pluginVariables.addPlayerName(event.getPlayer().getName());
		patternVariables.reloadNamesPattern();
	}
}
