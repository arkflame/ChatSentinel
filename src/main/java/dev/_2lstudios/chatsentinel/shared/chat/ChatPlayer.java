package dev._2lstudios.chatsentinel.shared.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev._2lstudios.chatsentinel.shared.interfaces.Module;

public class ChatPlayer {
    private final UUID uuid;
    private Map<Module, Integer> warns;
    private String[] lastMessages;
    private long lastMessageTime;

    public ChatPlayer(final UUID uuid) {
        this.uuid = uuid;
        this.warns = new HashMap<>();
        this.lastMessages = new String[3];
        this.lastMessageTime = 0;
    }

    public int getWarns(final Module module) {
        return this.warns.getOrDefault(module, 0);
    }

    public int addWarn(final Module module) {
        final int warns = this.warns.getOrDefault(module, 0) + 1;

        this.warns.put(module, warns);

        return warns;
    }

    public boolean isLastMessage(final String message) {
        if (message != null) {
            for (final String lastMessage : lastMessages) {

                if (message.equals(lastMessage)) {
                    return true;
                }
            }
        }

        return false;
    }

    public long getLastMessageTime() {
        return this.lastMessageTime;
    }

    public void addLastMessage(final String lastMessage, final long lastMessageTime) {
        this.lastMessages[2] = this.lastMessages[1];
        this.lastMessages[1] = this.lastMessages[0];
        this.lastMessages[0] = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public void clearWarns() {
        this.warns.clear();
    }

    public UUID getUniqueId() {
        return uuid;
    }
}