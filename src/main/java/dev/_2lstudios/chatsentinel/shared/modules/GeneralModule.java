package dev._2lstudios.chatsentinel.shared.modules;

import java.util.Collection;

public class GeneralModule {
	private Collection<String> commands;

	public void loadData(final Collection<String> commands) {
		this.commands = commands;
	}

	public boolean isCommand(final String message) {
		for (final String command : commands) {
			if (message.toLowerCase().startsWith(command + ' '))
				return true;
		}

		return false;
	}
}
