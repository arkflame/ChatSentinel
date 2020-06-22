package twolovers.chatsentinel.shared.interfaces;

import twolovers.chatsentinel.shared.chat.ChatPlayer;

public interface Module {
    public boolean meetsCondition(final ChatPlayer chatPlayer, final String message);

    public int getMaxWarns();

    public String[] getCommands(final String[][] placeholders);

    public String getName();

    public String getWarnNotification(final String[][] placeholders);
}