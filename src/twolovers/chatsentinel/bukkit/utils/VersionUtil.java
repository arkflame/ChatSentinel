package twolovers.chatsentinel.bukkit.utils;

public class VersionUtil {
	private static boolean oneDotNine = false;

	public static void start(final String version) {
		oneDotNine = !version.contains("1.8") && !version.contains("1.7");
	}

	public static boolean isOneDotNine() {
		return oneDotNine;
	}
}
