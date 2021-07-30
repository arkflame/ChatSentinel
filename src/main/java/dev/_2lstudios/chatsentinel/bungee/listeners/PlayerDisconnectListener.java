package dev._2lstudios.chatsentinel.bungee.listeners;

import java.util.UUID;

import dev._2lstudios.chatsentinel.bungee.modules.ModuleManager;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {
	final private WhitelistModule whitelistModule;
	final private ChatPlayerManager chatPlayerManager;

	public PlayerDisconnectListener(final ModuleManager moduleManager, final ChatPlayerManager chatPlayerManager) {
		this.whitelistModule = moduleManager.getWhitelistModule();
		this.chatPlayerManager = chatPlayerManager;
	}

	@EventHandler
	public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
		final ProxiedPlayer player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);

		this.chatPlayerManager.setOffline(chatPlayer);

		if (whitelistModule.isEnabled())
			this.whitelistModule.removeName(player.getName());
	}
}
