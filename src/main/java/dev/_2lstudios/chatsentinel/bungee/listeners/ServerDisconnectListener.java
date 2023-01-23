package dev._2lstudios.chatsentinel.bungee.listeners;

import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerDisconnectListener implements Listener {
    private GeneralModule generalModule;

    public ServerDisconnectListener(GeneralModule generalModule) {
        this.generalModule = generalModule;
    }

    @EventHandler
    public void onPlayerQuit(ServerDisconnectEvent event) {
        generalModule.removeNickname(event.getPlayer().getName());
    }
}
