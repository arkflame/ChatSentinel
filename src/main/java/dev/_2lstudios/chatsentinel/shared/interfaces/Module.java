package dev._2lstudios.chatsentinel.shared.interfaces;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;

public interface Module {
    public boolean meetsCondition(ChatPlayer chatPlayer, String message);

    public int getMaxWarns();

    public String[] getCommands(String[][] placeholders);

    public String getName();

    public String getWarnNotification(String[][] placeholders);
}