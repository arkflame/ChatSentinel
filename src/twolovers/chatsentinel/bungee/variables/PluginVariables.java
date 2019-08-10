package twolovers.chatsentinel.bungee.variables;


import net.md_5.bungee.api.connection.Connection;
import twolovers.chatsentinel.bungee.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginVariables {
	private final FloodVariables floodVariables;
	private final MessagesVariables messagesVariables;
	private final PatternVariables patternVariables;
	private final SwearingVariables swearingVariables;
	private final SyntaxVariables syntaxVariables;
	private final ThrottleVariables throttleVariables;

	final private List<String> playerNames = new ArrayList<>();
	final private Map<Connection, Long> throttle = new HashMap<>();
	final private Map<Connection, Integer> warns = new HashMap<>();
	final private Map<Connection, String> lastMessages = new HashMap<>();

	public PluginVariables(final ConfigUtil configUtil) {
		floodVariables = new FloodVariables(configUtil);
		messagesVariables = new MessagesVariables(configUtil);
		patternVariables = new PatternVariables(configUtil, this);
		swearingVariables = new SwearingVariables(configUtil);
		syntaxVariables = new SyntaxVariables(configUtil);
		throttleVariables = new ThrottleVariables(configUtil);

		reloadData();
	}

	final public FloodVariables getFloodVariables() {
		return floodVariables;
	}

	final public MessagesVariables getMessagesVariables() {
		return messagesVariables;
	}

	final public PatternVariables getPatternVariables() {
		return patternVariables;
	}

	final public SwearingVariables getSwearingVariables() {
		return swearingVariables;
	}

	final public SyntaxVariables getSyntaxVariables() {
		return syntaxVariables;
	}

	final public ThrottleVariables getThrottleVariables() {
		return throttleVariables;
	}

	final public void reloadData() {
		floodVariables.loadData();
		messagesVariables.loadData();
		patternVariables.loadData();
		swearingVariables.loadData();
		syntaxVariables.loadData();
		throttleVariables.loadData();
	}

	final public long getThrottle(final Connection connection) {
		return throttle.getOrDefault(connection, (long) 0);
	}

	final public void setThrottle(final Connection connection, final long input) {
		throttle.put(connection, input);
	}

	final public void removeThrottle(final Connection connection) {
		throttle.remove(connection);
	}

	final public void addWarn(final Connection connection) {
		warns.put(connection, warns.getOrDefault(connection, 0) + 1);
	}

	final public void removeWarns(final Connection connection) {
		warns.remove(connection);
	}

	final public int getWarns(final Connection connection) {
		return warns.getOrDefault(connection, 0);
	}

	final public void setLastMessage(final Connection connection, final String input) {
		lastMessages.put(connection, input);
	}

	final public void removeLastMessage(final Connection connection) {
		lastMessages.remove(connection);
	}

	final public void addPlayerName(String string) {
		playerNames.add(string);
	}

	final public void removePlayerName(String string) {
		playerNames.remove(string);
	}

	final List<String> getPlayerNames() {
		return playerNames;
	}

	final public String getLastMessage(final Connection connection) {
		return lastMessages.getOrDefault(connection, "defaultString");
	}
}
