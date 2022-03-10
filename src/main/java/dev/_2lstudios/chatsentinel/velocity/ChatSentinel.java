package dev._2lstudios.chatsentinel.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.velocity.commands.ChatSentinelCommand;
import dev._2lstudios.chatsentinel.velocity.listeners.ChatListener;
import dev._2lstudios.chatsentinel.velocity.modules.ModuleManager;
import dev._2lstudios.chatsentinel.velocity.utils.ConfigUtil;

import java.nio.file.Path;
import java.util.logging.Logger;

public class ChatSentinel {
	private final ProxyServer proxyServer;
	private final Logger logger;
	private final Path dataPath;
	private final ChatSentinel instance;

	@Inject
	public ChatSentinel(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataPath) {
		this.proxyServer = proxyServer;
		this.logger = logger;
		this.dataPath = dataPath;
		instance = this;
	}

	@Subscribe
	public void onInit(ProxyInitializeEvent event) {
		final ConfigUtil configUtil = new ConfigUtil(instance);

		configUtil.create("%datafolder%/config.yml");
		configUtil.create("%datafolder%/messages.yml");
		configUtil.create("%datafolder%/whitelist.yml");
		configUtil.create("%datafolder%/blacklist.yml");

		final ModuleManager moduleManager = new ModuleManager(proxyServer, configUtil);
		final ChatPlayerManager chatPlayerManager = new ChatPlayerManager();

		proxyServer.getEventManager().register(this, new ChatListener(proxyServer, moduleManager, chatPlayerManager));
		proxyServer.getCommandManager().register("chatsentinel", new ChatSentinelCommand(moduleManager, proxyServer), "cs");
	}

	public Logger getLogger() {
		return logger;
	}

	public Path getDataPath() {
		return dataPath;
	}
}