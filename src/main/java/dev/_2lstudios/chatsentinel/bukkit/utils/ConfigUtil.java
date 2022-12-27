package dev._2lstudios.chatsentinel.bukkit.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

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

	public YamlConfiguration get(String filePath) {
		File dataFolder = plugin.getDataFolder();
		File file = new File(filePath.replace("%datafolder%", dataFolder.toPath().toString()));

		if (file.exists())
			return YamlConfiguration.loadConfiguration(file);
		else
			return new YamlConfiguration();
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