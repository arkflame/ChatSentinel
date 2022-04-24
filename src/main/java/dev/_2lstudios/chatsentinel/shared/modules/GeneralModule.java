package dev._2lstudios.chatsentinel.shared.modules;

import java.util.Collection;

public class GeneralModule {
	private boolean sanitize;
	private Collection<String> commands;

	public void loadData(final boolean sanitize, final Collection<String> commands) {
		this.sanitize = sanitize;
		this.commands = commands;
	}

    public boolean isSanitizeEnabled() {
        return sanitize;
    }

	public boolean isCommand(String message) {
		message = message.toLowerCase();

		for (final String command : commands) {
			if (message.startsWith(command + ' '))
				return true;
		}

		return false;
	}
}
