package dev._2lstudios.chatsentinel.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

import dev._2lstudios.chatsentinel.velocity.ChatSentinel;

public class ChatSentinelCommand {
    private ChatSentinelCommand() {}
    public static void register(CommandManager commandManager, ChatSentinel plugin) {
        final BrigadierCommand command = new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("chatsentinel")
            .executes(cmd -> {
                return Command.SINGLE_SUCCESS;
            })
        );

        CommandMeta meta = commandManager.metaBuilder("chatsentinel")
            .plugin(plugin)
            .build();
        
        commandManager.register(meta, command);
    }
}
