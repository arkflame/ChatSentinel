package dev._2lstudios.chatsentinel.bungee.listeners;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerConnectListener implements Listener {
    private ChatPlayerManager chatPlayerManager;

    public ServerConnectListener(ChatPlayerManager chatPlayerManager) {
        this.chatPlayerManager = chatPlayerManager;
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (event.isCancelled()) return;
        
        ChatPlayer chatPlayer = chatPlayerManager.getPlayer(event.getPlayer());

        if (chatPlayer != null) {
            // Reset the locale of the player if already exists
            chatPlayer.setLocale(null);
        }
    }
}
