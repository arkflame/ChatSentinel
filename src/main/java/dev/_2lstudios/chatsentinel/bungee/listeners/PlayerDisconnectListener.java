package dev._2lstudios.chatsentinel.bungee.listeners;

import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {
    private GeneralModule generalModule;

    public PlayerDisconnectListener(GeneralModule generalModule) {
        this.generalModule = generalModule;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        generalModule.removeNickname(event.getPlayer().getName());
    }
}
