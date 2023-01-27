package dev._2lstudios.chatsentinel.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;

public class PlayerJoinListener implements Listener {
    private GeneralModule generalModule;
    private ChatPlayerManager chatPlayerManager;

    public PlayerJoinListener(GeneralModule generalModule, ChatPlayerManager chatPlayerManager) {
        this.generalModule = generalModule;
        this.chatPlayerManager = chatPlayerManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ChatPlayer chatPlayer = chatPlayerManager.getPlayerOrCreate(event.getPlayer());

        if (chatPlayer != null) {
            // Reset the locale of the player if already exists
            chatPlayer.setLocale(null);

            // Add the nickname
            generalModule.addNickname(event.getPlayer().getName());
        }
    }
}
