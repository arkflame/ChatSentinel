package dev._2lstudios.chatsentinel.shared.interfaces;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;

public interface Module {
    public boolean meetsCondition(final ChatPlayer chatPlayer, final String message);

    public int getMaxWarns();

    public String[] getCommands(final String[][] placeholders);

    public String getName();

    public String getWarnNotification(final String[][] placeholders);
}