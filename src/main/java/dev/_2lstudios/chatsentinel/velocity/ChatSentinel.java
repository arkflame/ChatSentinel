package dev._2lstudios.chatsentinel.velocity;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.ModuleManager;
import dev._2lstudios.chatsentinel.velocity.commands.ChatSentinelCommand;
import dev._2lstudios.chatsentinel.velocity.listeners.ChatListener;
import dev._2lstudios.chatsentinel.velocity.listeners.CommandListener;
import dev._2lstudios.chatsentinel.velocity.modules.VelocityModuleManager;
import dev._2lstudios.chatsentinel.velocity.utils.ConfigUtils;

@Plugin(
  id = "chatsentinel",
  name = "ChatSentinel",
  version = "0.4.7",
  authors = ("2LS"),
  url = "https://builtbybit.com/resources/23698/",
  description = "Advanced chat management plugin"
)
public class ChatSentinel {
    private final ProxyServer proxy;
    private final EventManager eventManager;
    private final CommandManager commandManager;
    private final Path path;
    private ModuleManager moduleManager;
    private ChatPlayerManager chatPlayerManager;

    @Inject
    public ChatSentinel(ProxyServer proxy, EventManager eventManager, CommandManager commandManager, @DataDirectory Path path) {
        this.proxy = proxy;
        this.eventManager = eventManager;
        this.commandManager = commandManager;
        chatPlayerManager = new ChatPlayerManager();
        this.path = path;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
      final ConfigUtils configs = new ConfigUtils(path);
      configs.init();

      this.moduleManager = new VelocityModuleManager(configs);
      moduleManager.reloadData();

      ChatSentinelCommand.register(commandManager, this);

      eventManager.register(this, new ChatListener(moduleManager, chatPlayerManager, proxy));
      eventManager.register(this, new CommandListener(moduleManager, chatPlayerManager, proxy));
    }
}
