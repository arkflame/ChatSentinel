package twolovers.chatsentinel.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import twolovers.chatsentinel.bukkit.variables.PatternVariables;
import twolovers.chatsentinel.bukkit.variables.PluginVariables;

public class PlayerQuitListener implements Listener {
	final private PluginVariables pluginVariables;

	public PlayerQuitListener(final PluginVariables pluginVariables) {
		this.pluginVariables = pluginVariables;
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final PatternVariables patternVariables = pluginVariables.getPatternVariables();
		final Player player = event.getPlayer();

		pluginVariables.removeThrottle(player);
		pluginVariables.removeLastMessage(player);
		pluginVariables.removeWarns(player);
		pluginVariables.removePlayerName(player.getName());
		patternVariables.reloadNamesPattern();
	}
}
