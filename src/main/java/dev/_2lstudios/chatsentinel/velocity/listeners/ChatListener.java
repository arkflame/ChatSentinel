package dev._2lstudios.chatsentinel.velocity.listeners;

import java.util.UUID;
import java.util.regex.Pattern;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.chat.ChatPlayerManager;
import dev._2lstudios.chatsentinel.shared.modules.BlacklistModule;
import dev._2lstudios.chatsentinel.shared.modules.CapsModule;
import dev._2lstudios.chatsentinel.shared.modules.CooldownModule;
import dev._2lstudios.chatsentinel.shared.modules.FloodModule;
import dev._2lstudios.chatsentinel.shared.modules.GeneralModule;
import dev._2lstudios.chatsentinel.shared.modules.MessagesModule;
import dev._2lstudios.chatsentinel.shared.modules.ModuleManager;
import dev._2lstudios.chatsentinel.shared.modules.SyntaxModule;
import dev._2lstudios.chatsentinel.shared.modules.WhitelistModule;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;
import dev._2lstudios.chatsentinel.velocity.utils.Components;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;

public class ChatListener {
    private final ModuleManager moduleManager;
    private final ChatPlayerManager chatPlayerManager;
    private final ProxyServer proxy;

    public ChatListener(ModuleManager moduleManager, ChatPlayerManager chatPlayerManager, ProxyServer proxy) {
        this.moduleManager = moduleManager;
        this.chatPlayerManager = chatPlayerManager;
        this.proxy = proxy;
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!event.getResult().isAllowed()) {
            return;
        }

        // Minecraft 1.19.1+ clients with valid key
        if(player.getIdentifiedKey() != null && player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) >= 0) {
            return;
        }

        if (player.hasPermission("chatsentinel.bypass")) {
            return;
        }

        final UUID uuid = player.getUniqueId();
        final GeneralModule generalModule = moduleManager.getGeneralModule();
        final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
        final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);
        final String originalMessage = event.getMessage().trim();
        String message = originalMessage;

        if (generalModule.isSanitizeEnabled()) {
            message = generalModule.sanitize(message);
        }

        if (generalModule.isSanitizeNames()) {
            message = generalModule.sanitizeNames(proxy, message);
        }

        if (whitelistModule.isEnabled()) {
            final Pattern whitelistPattern = whitelistModule.getPattern();

            message = whitelistPattern.matcher(message)
                    .replaceAll("");
        }

        message = message.trim();

        final MessagesModule messagesModule = moduleManager.getMessagesModule();
        final String lang = VersionUtil.getLocale(player);

        processModule(player, chatPlayer, messagesModule, moduleManager.getCapsModule(), event,
                message, originalMessage, lang);
        processModule(player, chatPlayer, messagesModule, moduleManager.getCooldownModule(), event,
                message, originalMessage, lang);
        processModule(player, chatPlayer, messagesModule, moduleManager.getFloodModule(), event,
                message, originalMessage, lang);
        processModule(player, chatPlayer, messagesModule, moduleManager.getBlacklistModule(), event,
                message, originalMessage, lang);
        processModule(player, chatPlayer, messagesModule, moduleManager.getSyntaxModule(), event,
                message, originalMessage, lang);

        if (!event.getResult().isAllowed()) {
            final long currentMillis = System.currentTimeMillis();

            chatPlayer.addLastMessage(message, currentMillis);
            moduleManager.getCooldownModule().setLastMessage(message, currentMillis);
        }
    }

    private void processModule(Player player, ChatPlayer chatPlayer,
			MessagesModule messagesModule, Module module, PlayerChatEvent event, String message,
			String originalMessage, String lang) {
		if (player.hasPermission("chatsentinel.bypass." + module.getName()) || !module.meetsCondition(chatPlayer, message)) {
            return;
        }

        final int warns = chatPlayer.addWarn(module);
        final int maxWarns = module.getMaxWarns();
        final String[][] placeholders = {
                { "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%", "%server%" },
                { player.getUsername(), originalMessage, String.valueOf(warns),
                        String.valueOf(maxWarns), String.valueOf(0), player.getCurrentServer().map(sv -> sv.getServerInfo().getName()).orElse("") } };

        if (module instanceof SyntaxModule) {
            event.setResult(ChatResult.denied());
        } else if (module instanceof BlacklistModule) {
            final BlacklistModule blacklistModule = (BlacklistModule) module;

            if (blacklistModule.isHideWords()) {
                event.setResult(ChatResult.message(
                        blacklistModule.getPattern().matcher(event.getMessage()).replaceAll("***")));
            } else
                event.setResult(ChatResult.denied());
        } else if (module instanceof CapsModule) {
            final CapsModule capsModule = (CapsModule) module;

            event.setResult(capsModule.isReplace()
                ? ChatResult.message(event.getMessage().toLowerCase())
                : ChatResult.denied());
        } else if (module instanceof CooldownModule) {
            placeholders[1][4] = String.valueOf(
                    ((CooldownModule) module).getRemainingTime(chatPlayer, event.getMessage()));

            event.setResult(ChatResult.denied());
        } else if (module instanceof FloodModule) {
            final FloodModule floodModule = (FloodModule) module;

            if (floodModule.isReplace()) {
                final String replacedString = floodModule.replace(event.getMessage());

                event.setResult(replacedString.isEmpty()
                    ? ChatResult.denied()
                    : ChatResult.message(replacedString));
            } else {
                event.setResult(ChatResult.denied());
            }
        } else {
            event.setResult(ChatResult.denied());
        }

        final CommandSource console = proxy.getConsoleCommandSource();
        final String notificationMessage = module.getWarnNotification(placeholders);
        final String warnMessage = messagesModule.getWarnMessage(placeholders, lang,
                module.getName());

        if (warnMessage != null && !warnMessage.isEmpty()) {
            player.sendMessage(Components.SERIALIZER.deserialize(warnMessage));
        }

        if (notificationMessage != null && !notificationMessage.isEmpty()) {
            for (final Player player1 : proxy.getAllPlayers()) {
                if (player1.hasPermission("chatsentinel.notify")) {
                    player1.sendMessage(Components.SERIALIZER.deserialize(notificationMessage));
                }
            }

            console.sendMessage(Components.SERIALIZER.deserialize(notificationMessage));
        }

        if (warns >= maxWarns && maxWarns > 0) {
            final CommandManager pluginManager = proxy.getCommandManager();

            for (final String command : module.getCommands(placeholders)) {
                pluginManager.executeAsync(console, command);
            }

            chatPlayer.clearWarns();
		}
	}
}
