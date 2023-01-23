package dev._2lstudios.chatsentinel.shared.chat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev._2lstudios.chatsentinel.shared.interfaces.Module;

public class ChatPlayer {
    private int historySize = 3;
    private UUID uuid;
    private Map<Module, Integer> warns;
    private Deque<String> lastMessages;
    private String locale = null;
    private long lastMessageTime;

    public ChatPlayer(UUID uuid) {
        this.uuid = uuid;
        this.warns = new HashMap<>();
        this.lastMessages = new ArrayDeque<>(historySize);
        this.lastMessageTime = 0;
    }

    public int getWarns(Module module) {
        return this.warns.getOrDefault(module, 0);
    }

    public int addWarn(Module module) {
        int warns = this.warns.getOrDefault(module, 0) + 1;

        this.warns.put(module, warns);

        return warns;
    }

    public String removeDigits(String str) {
        // Converting the given string
        // into a character array
        char[] charArray = str.toCharArray();
        String result = "";

        // Traverse the character array
        for (int i = 0; i < charArray.length; i++) {
            // Check if the specified character is not digit
            // then add this character into result variable
            if (!Character.isDigit(charArray[i])) {
                result = result + charArray[i];
            }
        }

        return result;
    }

    public boolean isLastMessage(String message) {
        // Check if message is null
        if (message != null) {
            // Remove digits from message
            message = removeDigits(message);

            // Get the length of the message
            int length = message.length();

            // Iterate over last messages
            for (String lastMessage : lastMessages) {
                // Check if equals the last message
                if (message.equals(lastMessage)) {
                    return true;
                }
                // Check if equals last message length
                if (length > 16 && length == lastMessage.length()) {
                    return true;
                }
            }
        }

        return false;
    }

    public long getLastMessageTime() {
        return this.lastMessageTime;
    }

    public void addLastMessage(String lastMessage, long lastMessageTime) {
        if (lastMessages.size() > historySize) {
            lastMessages.removeLast();
        }
        lastMessages.offerFirst(removeDigits(lastMessage));
        this.lastMessageTime = lastMessageTime;
    }

    public void clearWarns() {
        this.warns.clear();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getLocale() {
        return hasLocale() ? locale : "en";
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean hasLocale() {
        return this.locale != null;
    }
}