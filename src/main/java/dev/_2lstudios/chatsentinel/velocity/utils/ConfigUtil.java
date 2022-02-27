package dev._2lstudios.chatsentinel.velocity.utils;

import dev._2lstudios.chatsentinel.velocity.ChatSentinel;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigUtil {
	final private ChatSentinel chatSentinel;

	public ConfigUtil() {
		this.chatSentinel = ChatSentinel.getInstance();
	}

	public YamlFile get(String file) {
		file = file.replace("%datafolder%", chatSentinel.getDataPath().toString());

		final YamlFile yamlFile = new YamlFile(file);
		try {
			yamlFile.load();
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
		return yamlFile;
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
					chatSentinel.getLogger().log(Level.INFO, ("[%pluginname%] File " + configFile + " has been created!")
							.replace("%pluginname%", chatSentinel.getPlugin().getDescription().getName().get()));
				} else
					configFile.createNewFile();
			}
		} catch (final IOException e) {
			chatSentinel.getLogger().log(Level.INFO, ("[%pluginname%] Unable to create configuration file!")
					.replace("%pluginname%", chatSentinel.getPlugin().getDescription().getName().get()));
		}
	}
}