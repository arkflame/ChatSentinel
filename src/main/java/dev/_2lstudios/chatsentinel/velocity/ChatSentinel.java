package dev._2lstudios.chatsentinel.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.ModuleManager;

public class ChatSentinel {
    private final ProxyServer proxy;
    private final EventManager eventManager;
    private final CommandManager commandManager;
    private ModuleManager moduleManager;
    private ChatPlayerManager chatPlayerManager;

    @Inject
    public ChatSentinel(ProxyServer proxy, EventManager eventManager, CommandManager commandManager) {
        this.proxy = proxy;
        this.eventManager = eventManager;
        this.commandManager = commandManager;
        chatPlayerManager = new ChatPlayerManager();
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

		/*pluginManager.registerListener(this, new ChatListener(this, moduleManager, chatPlayerManager));
		pluginManager.registerCommand(this, new ChatSentinelCommand(moduleManager, server));*/
    }
}
