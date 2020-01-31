package twolovers.chatsentinel.bukkit.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigUtil {
	final private Plugin plugin;

	public ConfigUtil(final Plugin plugin) {
		this.plugin = plugin;
	}

	public YamlConfiguration get(String filePath) {
		final File dataFolder = plugin.getDataFolder();
		final File file = new File(filePath.replace("%datafolder%", dataFolder.toPath().toString()));

		if (file.exists())
			return YamlConfiguration.loadConfiguration(file);
		else
			return new YamlConfiguration();
	}

	public void create(String file) {
		try {
			final File dataFolder = plugin.getDataFolder();

			file = file.replace("%datafolder%", dataFolder.toPath().toString());

			final File configFile = new File(file);

			if (!configFile.exists()) {
				final String[] files = file.split("/");
				final InputStream inputStream = plugin.getClass().getClassLoader()
						.getResourceAsStream(files[files.length - 1]);
				final File parentFile = configFile.getParentFile();

				if (parentFile != null)
					parentFile.mkdirs();

				if (inputStream != null) {
					Files.copy(inputStream, configFile.toPath());
					plugin.getLogger().log(Level.INFO, ("[%pluginname%] File " + configFile + " has been created!")
							.replace("%pluginname%", plugin.getDescription().getName()));
				} else
					configFile.createNewFile();
			}
		} catch (final IOException e) {
			plugin.getLogger().log(Level.INFO, ("[%pluginname%] Unable to create configuration file!")
					.replace("%pluginname%", plugin.getDescription().getName()));
		}
	}
}