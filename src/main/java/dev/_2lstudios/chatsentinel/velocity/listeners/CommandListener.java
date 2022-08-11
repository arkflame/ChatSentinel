package dev._2lstudios.chatsentinel.velocity.listeners;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
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

public class CommandListener {
    private final ModuleManager moduleManager;
    private final ChatPlayerManager chatPlayerManager;
    private final ProxyServer proxy;

    public CommandListener(ModuleManager moduleManager, ChatPlayerManager chatPlayerManager, ProxyServer proxy) {
        this.moduleManager = moduleManager;
        this.chatPlayerManager = chatPlayerManager;
        this.proxy = proxy;
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        if(!event.getResult().isAllowed()) {
            return;
        }

        final CommandSource source = event.getCommandSource();

        if(!(source instanceof Player)) {
            return;
        }

        final Player player = (Player)source;
        final UUID uuid = player.getUniqueId();
        final GeneralModule generalModule = moduleManager.getGeneralModule();
        final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
        final ChatPlayer chatPlayer = chatPlayerManager.getPlayer(uuid);
        final String originalCommand = event.getCommand().trim();
        String command = originalCommand;

        if (generalModule.isSanitizeEnabled()) {
            command = generalModule.sanitize(command);
        }

        if (generalModule.isSanitizeNames()) {
            command = generalModule.sanitizeNames(proxy, command);
        }

        if (whitelistModule.isEnabled()) {
            final Pattern whitelistPattern = whitelistModule.getPattern();

            command = whitelistPattern.matcher(command)
                    .replaceAll("");
        }

        final MessagesModule messagesModule = moduleManager.getMessagesModule();
        final String lang = VersionUtil.getLocale(player);

        processModule(player, chatPlayer, messagesModule, moduleManager.getCooldownModule(), event,
                command, originalCommand, lang);

        processModule(player, chatPlayer, messagesModule, moduleManager.getSyntaxModule(), event,
                command, originalCommand, lang);   
                
        if (!event.getResult().isAllowed()) {
            final long currentMillis = System.currentTimeMillis();

            chatPlayer.addLastMessage(command, currentMillis);
            moduleManager.getCooldownModule().setLastMessage(command, currentMillis);
        }
    }

    private void processModule(Player player, ChatPlayer chatPlayer,
			MessagesModule messagesModule, Module module, CommandExecuteEvent event, String message,
			String originalCommand, String lang) {
		if (player.hasPermission("chatsentinel.bypass." + module.getName()) || !module.meetsCondition(chatPlayer, message)) {
            return;
        }

        final int warns = chatPlayer.addWarn(module);
        final int maxWarns = module.getMaxWarns();
        final String[][] placeholders = {
                { "%player%", "%message%", "%warns%", "%maxwarns%", "%cooldown%", "%server%" },
                { player.getUsername(), originalCommand, Integer.toString(warns),
                    Integer.toString(maxWarns), Integer.toString(0), player.getCurrentServer().get().getServerInfo().getName() } };

        if (module instanceof SyntaxModule) {
            event.setResult(CommandResult.denied());
        } else if (module instanceof BlacklistModule) {
            final BlacklistModule blacklistModule = (BlacklistModule) module;

            if (blacklistModule.isHideWords()) {
                event.setResult(CommandResult.command(
                        blacklistModule.getPattern().matcher(event.getCommand()).replaceAll("***")));
            } else
                event.setResult(CommandResult.denied());
        } else if (module instanceof CapsModule) {
            final CapsModule capsModule = (CapsModule) module;

            event.setResult(capsModule.isReplace()
                ? CommandResult.command(event.getCommand().toLowerCase(Locale.ROOT))
                : CommandResult.denied());
        } else if (module instanceof CooldownModule) {
            placeholders[1][4] = String.valueOf(
                    ((CooldownModule) module).getRemainingTime(chatPlayer, event.getCommand()));

            event.setResult(CommandResult.denied());
        } else if (module instanceof FloodModule) {
            final FloodModule floodModule = (FloodModule) module;

            if (floodModule.isReplace()) {
                final String replacedString = floodModule.replace(event.getCommand());

                event.setResult(replacedString.isEmpty()
                    ? CommandResult.denied()
                    : CommandResult.command(replacedString));
            } else {
                event.setResult(CommandResult.denied());
            }
        } else {
            event.setResult(CommandResult.denied());
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
