package dev._2lstudios.chatsentinel.bukkit.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

			final Path configFile = Paths.get(file);

			if (Files.notExists(configFile)) {
				final String[] files = file.split("/");
				final Path parentFile = configFile.getParent();

				if (parentFile != null)
					Files.createDirectory(parentFile);
				try (InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(files[files.length - 1])) {
					if (inputStream != null) {
						Files.copy(inputStream, configFile);
						plugin.getLogger().log(Level.INFO, "[{0}] File {1} has been created!", new Object[] {
							plugin.getDescription().getName(), configFile
						});
					}
				}
			}
		} catch (final IOException e) {
			plugin.getLogger().log(Level.INFO, "[{0}] Unable to create configuration file!", plugin.getDescription().getName());
		}
	}
}