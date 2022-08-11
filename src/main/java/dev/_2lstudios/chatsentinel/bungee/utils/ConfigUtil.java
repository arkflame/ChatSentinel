package dev._2lstudios.chatsentinel.bungee.utils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

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

	public Configuration get(String file) {
		final File dataFolder = plugin.getDataFolder();

		file = file.replace("%datafolder%", dataFolder.toPath().toString());

		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(file));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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
						plugin.getLogger().log(Level.INFO, "[{0}] File {1} has been created!", new Object[]{
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