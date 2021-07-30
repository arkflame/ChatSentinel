package dev._2lstudios.chatsentinel.bungee.listeners;

import dev._2lstudios.chatsentinel.bungee.modules.ModuleManager;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {
	final private WhitelistModule whitelistModule;
	final private ChatPlayerManager chatPlayerManager;

	public PostLoginListener(final ModuleManager moduleManager, final ChatPlayerManager chatPlayerManager) {
		this.whitelistModule = moduleManager.getWhitelistModule();
		this.chatPlayerManager = chatPlayerManager;
	}

	@EventHandler
	public void onPostLogin(final PostLoginEvent event) {
		final ProxiedPlayer player = event.getPlayer();
		final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(player.getUniqueId());

		chatPlayerManager.setOnline(chatPlayer);

		if (whitelistModule.isEnabled())
			whitelistModule.addName(player.getName());
	}
}
