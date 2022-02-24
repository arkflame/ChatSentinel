package dev._2lstudios.chatsentinel.shared.modules;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;

public class CooldownModule implements Module {
	private boolean enabled;
	private int repeatTimeGlobal, repeatTime, normalTime, commandTime;
	private long lastMessageTime = 0L;
	private String lastMessage = "";

	final public void loadData(final boolean enabled, final int repeatTimeGlobal, final int repeatTime, final int normalTime,
			final int commandTime) {
		this.enabled = enabled;
		this.repeatTimeGlobal = repeatTimeGlobal;
		this.repeatTime = repeatTime;
		this.normalTime = normalTime;
		this.commandTime = commandTime;
	}

	final public float getRemainingTime(final ChatPlayer chatPlayer, final String message) {
		if (this.enabled && message != null) {
			final long currentTime = System.currentTimeMillis();
			final long lastMessageTime = currentTime - chatPlayer.getLastMessageTime();
			final long lastMessageTimeGlobal = currentTime - this.lastMessageTime;
			final long remainingTime;

			if (message.startsWith("/")) {
				remainingTime = this.commandTime - lastMessageTime;
			} else if (chatPlayer.isLastMessage(message) && lastMessageTime < this.repeatTime) {
				remainingTime = this.repeatTime - lastMessageTime;
			} else if (this.lastMessage.equals(message) && lastMessageTimeGlobal < this.repeatTimeGlobal) {
				remainingTime = this.repeatTimeGlobal - lastMessageTimeGlobal;
			} else {
				remainingTime = this.normalTime - lastMessageTime;
			}

			return ((float) (remainingTime / 100)) / 10;
		}

		return 0;
	}

	@Override
	public boolean meetsCondition(final ChatPlayer chatPlayer, final String message) {
		return (getRemainingTime(chatPlayer, message) > 0);
	}

	@Override
	final public String getName() {
		return "Cooldown";
	}

	@Override
	final public String[] getCommands(final String[][] placeholders) {
		return new String[0];
	}

	@Override
	final public String getWarnNotification(final String[][] placeholders) {
		return null;
	}

	@Override
	public int getMaxWarns() {
		return 0;
	}

    public void setLastMessage(String lastMessage, long lastMessageTime) {
		this.lastMessage = lastMessage;
		this.lastMessageTime = lastMessageTime;
    }
}
