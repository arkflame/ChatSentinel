package dev._2lstudios.chatsentinel.shared.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import dev._2lstudios.chatsentinel.shared.utils.VersionUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatPlayerManager {
    private Map<UUID, ChatPlayer> chatPlayers = new HashMap<>();

    public ChatPlayer getPlayerOrCreate(ProxiedPlayer player) {
        ChatPlayer chatPlayer = getPlayerOrCreate(player.getUniqueId());

        if (!chatPlayer.hasLocale()) {
            chatPlayer.setLocale(VersionUtil.getLocale(player));
        }

        return chatPlayer;
    }

    public ChatPlayer getPlayerOrCreate(Player player) {
        ChatPlayer chatPlayer = getPlayerOrCreate(player.getUniqueId());

        if (!chatPlayer.hasLocale()) {
            chatPlayer.setLocale(VersionUtil.getLocale(player));
        }

        return chatPlayer;
    }

    public ChatPlayer getPlayerOrCreate(UUID uuid) {
        ChatPlayer chatPlayer = getPlayer(uuid);

        if (chatPlayer == null) {
            chatPlayer = new ChatPlayer(uuid);
            chatPlayers.put(uuid, chatPlayer);
        }

        return chatPlayer;
    }

    public ChatPlayer getPlayer(ProxiedPlayer player) {
        return getPlayer(player.getUniqueId());
    }

    public ChatPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public ChatPlayer getPlayer(UUID uuid) {
        return chatPlayers.getOrDefault(uuid, null);
    }
}