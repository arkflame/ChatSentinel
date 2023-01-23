package dev._2lstudios.chatsentinel.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;

public class PlayerQuitListener implements Listener {
    private GeneralModule generalModule;

    public PlayerQuitListener(GeneralModule generalModule) {
        this.generalModule = generalModule;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        generalModule.removeNickname(event.getPlayer().getName());
    }
}
