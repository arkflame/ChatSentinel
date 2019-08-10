package twolovers.chatsentinel.bungee.utils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigUtil {
	final private Plugin plugin;

	public ConfigUtil(final Plugin plugin) {
		this.plugin = plugin;
	}

	public void createFile(final String file) {
		try {
			final File dataFolder = plugin.getDataFolder();
			final File configFile = new File(plugin.getDataFolder(), file);

			if (!plugin.getDataFolder().exists() && dataFolder.mkdir()) {
				System.out.println("[ChatSentinel] Data folder has been created!");
			}

			if (!configFile.exists()) {
				final Path newConfigPath = configFile.toPath();
				final InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(file);

				if (inputStream != null) {
					Files.copy(inputStream, newConfigPath);
					System.out.println("[ChatSentinel] File " + configFile + " has been created!");
				}
			}
		} catch (IOException e) {
			System.out.println("[ChatSentinel] Unable to create configuration file!");
		}
	}

	final public Configuration getConfig(final String file) {
		final File configFile = new File(plugin.getDataFolder(), file);

		if (!configFile.exists()) {
			createFile(file);
		}

		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
		} catch (IOException e) {
			System.out.println("[ChatSentinel] Unable to load configuration file!");
			return null;
		}
	}
}