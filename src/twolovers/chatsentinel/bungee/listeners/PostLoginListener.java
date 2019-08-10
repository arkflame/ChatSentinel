package twolovers.chatsentinel.bungee.listeners;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.chatsentinel.bungee.variables.PatternVariables;
import twolovers.chatsentinel.bungee.variables.PluginVariables;

public class PostLoginListener implements Listener {
	final private PluginVariables pluginVariables;

	public PostLoginListener(PluginVariables pluginVariables) {
		this.pluginVariables = pluginVariables;
	}

	@EventHandler
	public void onPlayerJoin(final PostLoginEvent event) {
		final PatternVariables patternVariables = pluginVariables.getPatternVariables();

		pluginVariables.addPlayerName(event.getPlayer().getName());
		patternVariables.reloadNamesPattern();
	}
}
