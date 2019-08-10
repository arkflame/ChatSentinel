package twolovers.chatsentinel.bukkit.variables;

import org.bukkit.entity.Player;
import twolovers.chatsentinel.bukkit.utils.ConfigUtil;

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
	private final CooldownVariables cooldownVariables;

	final private List<String> playerNames = new ArrayList<>();
	final private Map<Player, Long> throttle = new HashMap<>();
	final private Map<Player, Integer> warns = new HashMap<>();
	final private Map<Player, String> lastMessages = new HashMap<>();

	public PluginVariables(final ConfigUtil configUtil) {
		floodVariables = new FloodVariables(configUtil);
		messagesVariables = new MessagesVariables(configUtil);
		patternVariables = new PatternVariables(configUtil, this);
		swearingVariables = new SwearingVariables(configUtil);
		syntaxVariables = new SyntaxVariables(configUtil);
		cooldownVariables = new CooldownVariables(configUtil);

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

	final public CooldownVariables getCooldownVariables() {
		return cooldownVariables;
	}

	final public void reloadData() {
		floodVariables.loadData();
		messagesVariables.loadData();
		patternVariables.loadData();
		swearingVariables.loadData();
		syntaxVariables.loadData();
		cooldownVariables.loadData();
	}

	final public long getThrottle(final Player player) {
		return throttle.getOrDefault(player, (long) 0);
	}

	final public void setThrottle(final Player player, final long input) {
		throttle.put(player, input);
	}

	final public void removeThrottle(final Player player) {
		throttle.remove(player);
	}

	final public void addWarn(final Player player) {
		warns.put(player, warns.getOrDefault(player, 0) + 1);
	}

	final public void removeWarns(final Player player) {
		warns.remove(player);
	}

	final public int getWarns(final Player player) {
		return warns.getOrDefault(player, 0);
	}

	final public void setLastMessage(final Player player, final String input) {
		lastMessages.put(player, input);
	}

	final public void removeLastMessage(final Player player) {
		lastMessages.remove(player);
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

	final public String getLastMessage(final Player player) {
		return lastMessages.getOrDefault(player, "defaultString");
	}
}
