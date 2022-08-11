package dev._2lstudios.chatsentinel.velocity.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

public class ConfigUtils {
    private final Path path;
    public ConfigUtils(Path path) {
        this.path = path;
    }

    public void init() {
        if(Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }
    }

    public ConfigurationNode createAndGet(String string) {
        Path config = path.resolve(string);
        if (Files.notExists(config)) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(string)) {
                Files.copy(in, config);
            } catch (IOException e) {
                e.printStackTrace();
                return ConfigurationNode.root();
            }
        }

        final YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder()
            .setPath(config)
            .build();

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return ConfigurationNode.root();
        }
    }
}
