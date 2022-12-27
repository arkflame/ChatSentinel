package dev._2lstudios.chatsentinel.shared.modules;

import dev._2lstudios.chatsentinel.shared.chat.ChatPlayer;
import dev._2lstudios.chatsentinel.shared.interfaces.Module;

public class CooldownModule implements Module {
	private boolean enabled;
	private int repeatTimeGlobal, repeatTime, normalTime, commandTime;
	private long lastMessageTime = 0L;
	private String lastMessage = "";

	public void loadData(boolean enabled, int repeatTimeGlobal, int repeatTime,
			int normalTime,
			int commandTime) {
		this.enabled = enabled;
		this.repeatTimeGlobal = repeatTimeGlobal;
		this.repeatTime = repeatTime;
		this.normalTime = normalTime;
		this.commandTime = commandTime;
	}

	public float getRemainingTime(ChatPlayer chatPlayer, String message) {
		if (this.enabled && message != null) {
			long currentTime = System.currentTimeMillis();
			long lastMessageTime = currentTime - chatPlayer.getLastMessageTime();
			long lastMessageTimeGlobal = currentTime - this.lastMessageTime;
			long remainingTime;

			if (message.startsWith("/")) {
				remainingTime = this.commandTime - lastMessageTime;
			} else if (chatPlayer.isLastMessage(message) && lastMessageTime < this.repeatTime) {
				remainingTime = this.repeatTime - lastMessageTime;
			} else if (this.lastMessage.equals(message) && lastMessageTimeGlobal < this.repeatTimeGlobal) {
				remainingTime = this.repeatTimeGlobal - lastMessageTimeGlobal;
			} else {
				remainingTime = this.normalTime - lastMessageTime;
			}

			if (remainingTime > 0) {
				return ((float) (remainingTime / 100)) / 10;
			}
		}

		return 0;
	}

	@Override
	public boolean meetsCondition(ChatPlayer chatPlayer, String message) {
		return getRemainingTime(chatPlayer, message) > 0;
	}

	@Override
	public String getName() {
		return "Cooldown";
	}

	@Override
	public String[] getCommands(String[][] placeholders) {
		return new String[0];
	}

	@Override
	public String getWarnNotification(String[][] placeholders) {
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
