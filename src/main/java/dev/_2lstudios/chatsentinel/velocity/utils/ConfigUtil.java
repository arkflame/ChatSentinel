package dev._2lstudios.chatsentinel.velocity.utils;

import dev._2lstudios.chatsentinel.velocity.ChatSentinel;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigUtil {
	final private ChatSentinel chatSentinel;

	public ConfigUtil(ChatSentinel chatSentinel) {
		this.chatSentinel = chatSentinel;
	}

	public ConfigurationNode get(String file) {

		file = file.replace("%datafolder%", chatSentinel.getDataPath().toString());

		try {
			YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath((new File(file)).toPath()).build();
			return loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void create(String file) {
		try {
			file = file.replace("%datafolder%", chatSentinel.getDataPath().toString());

			final File configFile = new File(file);

			if (!configFile.exists()) {
				final String[] files = file.split("/");
				final InputStream inputStream = chatSentinel.getClass().getClassLoader()
						.getResourceAsStream(files[files.length - 1]);
				final File parentFile = configFile.getParentFile();

				if (parentFile != null)
					parentFile.mkdirs();

				if (inputStream != null) {
					Files.copy(inputStream, configFile.toPath());
					chatSentinel.getLogger().info("File " + configFile + " has been created!");
				} else
					configFile.createNewFile();
			}
		} catch (final IOException e) {
			chatSentinel.getLogger().info("Unable to create configuration file!");
		}
	}
}