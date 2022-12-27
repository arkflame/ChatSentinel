package dev._2lstudios.chatsentinel.bungee.utils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigUtil {
	private Plugin plugin;

	public ConfigUtil(Plugin plugin) {
		this.plugin = plugin;
	}

	public Configuration get(String file) {
		File dataFolder = plugin.getDataFolder();

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
			File dataFolder = plugin.getDataFolder();

			file = file.replace("%datafolder%", dataFolder.toPath().toString());

			File configFile = new File(file);

			if (!configFile.exists()) {
				String[] files = file.split("/");
				InputStream inputStream = plugin.getClass().getClassLoader()
						.getResourceAsStream(files[files.length - 1]);
				File parentFile = configFile.getParentFile();

				if (parentFile != null)
					parentFile.mkdirs();

				if (inputStream != null) {
					Files.copy(inputStream, configFile.toPath());
					plugin.getLogger().log(Level.INFO, ("[%pluginname%] File " + configFile + " has been created!")
							.replace("%pluginname%", plugin.getDescription().getName()));
				} else
					configFile.createNewFile();
			}
		} catch (IOException e) {
			plugin.getLogger().log(Level.INFO, ("[%pluginname%] Unable to create configuration file!")
					.replace("%pluginname%", plugin.getDescription().getName()));
		}
	}
}