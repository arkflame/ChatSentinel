package twolovers.chatsentinel.shared.modules;

import twolovers.chatsentinel.shared.chat.ChatPlayer;
import twolovers.chatsentinel.shared.interfaces.Module;

public class CooldownModule implements Module {
	private boolean enabled;
	private int repeatTime, normalTime, commandTime;

	final public void loadData(final boolean enabled, final int repeatTime, final int normalTime,
			final int commandTime) {
		this.enabled = enabled;
		this.repeatTime = repeatTime;
		this.normalTime = normalTime;
		this.commandTime = commandTime;
	}

	final public float getRemainingTime(final ChatPlayer chatPlayer, final String message) {
		if (this.enabled && message != null) {
			final long currentTime = System.currentTimeMillis();
			final long lastMessageTime = currentTime - chatPlayer.getLastMessageTime();
			final long timeToWait;

			if ((message.startsWith("/"))) {
				timeToWait = this.commandTime;
			} else if (chatPlayer.isLastMessage(message) && lastMessageTime < this.repeatTime)
				timeToWait = this.repeatTime;
			else
				timeToWait = this.normalTime;

			return ((float) ((timeToWait - lastMessageTime) / 100)) / 10;
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
}
