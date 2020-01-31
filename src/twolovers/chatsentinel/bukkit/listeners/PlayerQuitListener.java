package twolovers.chatsentinel.bukkit.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import twolovers.chatsentinel.shared.chat.ChatPlayer;
import twolovers.chatsentinel.shared.chat.ChatPlayerManager;
import twolovers.chatsentinel.shared.modules.WhitelistModule;
import twolovers.chatsentinel.bukkit.modules.ModuleManager;

public class PlayerQuitListener implements Listener {
	final private WhitelistModule whitelistModule;
	final private ChatPlayerManager chatPlayerManager;

	public PlayerQuitListener(final ModuleManager moduleManager, final ChatPlayerManager chatPlayerManager) {
		this.whitelistModule = moduleManager.getWhitelistModule();
		this.chatPlayerManager = chatPlayerManager;
	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);

		this.chatPlayerManager.setOffline(chatPlayer);
		this.whitelistModule.removeName(player.getName());
	}
}
