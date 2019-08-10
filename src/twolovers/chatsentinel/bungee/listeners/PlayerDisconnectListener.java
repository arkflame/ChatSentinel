package twolovers.chatsentinel.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.chatsentinel.bungee.variables.PatternVariables;
import twolovers.chatsentinel.bungee.variables.PluginVariables;

public class PlayerDisconnectListener implements Listener {
	final private PluginVariables pluginVariables;

	public PlayerDisconnectListener(final PluginVariables pluginVariables) {
		this.pluginVariables = pluginVariables;
	}

	@EventHandler
	public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
		final PatternVariables patternVariables = pluginVariables.getPatternVariables();
		final ProxiedPlayer proxiedPlayer = event.getPlayer();

		pluginVariables.removeThrottle(proxiedPlayer);
		pluginVariables.removeLastMessage(proxiedPlayer);
		pluginVariables.removePlayerName(proxiedPlayer.getName());
		patternVariables.reloadNamesPattern();
	}
}
