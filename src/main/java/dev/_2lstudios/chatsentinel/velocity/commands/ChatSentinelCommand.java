package dev._2lstudios.chatsentinel.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import dev._2lstudios.chatsentinel.shared.modules.ModuleManager;
import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;
import dev._2lstudios.chatsentinel.velocity.ChatSentinel;
import dev._2lstudios.chatsentinel.velocity.utils.Components;

public class ChatSentinelCommand {
    private ChatSentinelCommand() {}
    public static void register(ProxyServer proxy, ChatSentinel plugin, ModuleManager moduleManager) {
        final BrigadierCommand command = new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("chatsentinel")
            .requires(src -> src.hasPermission("chatsentinel.admin"))
            .executes(cmd -> {
                cmd.getSource().sendMessage(
                    Components.SERIALIZER.deserialize(
                        moduleManager.getMessagesModule().getHelp(VersionUtil.getLocale(cmd.getSource()))));
                return Command.SINGLE_SUCCESS;
            })
            .then(LiteralArgumentBuilder.<CommandSource>literal("help")
                .executes(cmd -> {
                    cmd.getSource().sendMessage(
                        Components.SERIALIZER.deserialize(
                            moduleManager.getMessagesModule().getHelp(VersionUtil.getLocale(cmd.getSource()))));
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(LiteralArgumentBuilder.<CommandSource>literal("clear")
                .executes(cmd -> {
                    final StringBuilder emptyLines = new StringBuilder();
                    final String newLine = "\n ";
                    final String[][] placeholders = { { "%player%" }, { cmd.getSource() instanceof Player
                        ? ((Player)cmd.getSource()).getUsername() : "console" } };

                    for (int i = 0; i < 128; i++) {
                        emptyLines.append(newLine);
                    }

                    emptyLines.append(moduleManager.getMessagesModule().getCleared(placeholders, VersionUtil.getLocale(cmd.getSource())));

                    for (final Player player : proxy.getAllPlayers()) {
                        player.sendMessage(Components.SERIALIZER.deserialize(emptyLines.toString()));
                    }
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(cmd -> {
                    moduleManager.reloadData();

				    cmd.getSource().sendMessage(
                        Components.SERIALIZER.deserialize(
                            moduleManager.getMessagesModule().getReload(VersionUtil.getLocale(cmd.getSource()))));
                    return Command.SINGLE_SUCCESS;
                })
            )
        );

        CommandMeta meta = proxy.getCommandManager().metaBuilder("chatsentinel")
            .plugin(plugin)
            .build();
        
        proxy.getCommandManager().register(meta, command);
    }
}
