package twolovers.chatsentinel.shared.interfaces;

import twolovers.chatsentinel.shared.chat.ChatPlayer;

public interface Module {
    public boolean meetsCondition(final ChatPlayer chatPlayer, final String message);

    public int getMaxWarns();

    // This will return punishments if the player meets the module conditions.
    public String[] getCommands(final String[][] placeholders);

    public String getName();

    // Returns the notification message of the module.
    public String getWarnNotification(final String[][] placeholders);
}