package dev._2lstudios.chatsentinel.bungee.listeners;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {
    private GeneralModule generalModule;
    private ChatPlayerManager chatPlayerManager;

    public PostLoginListener(GeneralModule generalModule, ChatPlayerManager chatPlayerManager) {
        this.generalModule = generalModule;
        this.chatPlayerManager = chatPlayerManager;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ChatPlayer chatPlayer = chatPlayerManager.getPlayerOrCreate(player);

        if (chatPlayer != null) {
            // Reset the locale of the player if already exists
            chatPlayer.setLocale(null);

            // Set notifications
            chatPlayer.setNotify(player.hasPermission("chatsentinel.notify"));

            // Add the nickname
            generalModule.addNickname(player.getName());
        }
    }
}
