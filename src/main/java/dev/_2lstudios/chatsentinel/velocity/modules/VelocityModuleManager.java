package dev._2lstudios.chatsentinel.velocity.modules;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;

import dev._2lstudios.chatsentinel.shared.modules.ModuleManager;
import dev._2lstudios.chatsentinel.shared.utils.ArraysUtil;
import dev._2lstudios.chatsentinel.velocity.utils.ConfigUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class VelocityModuleManager extends ModuleManager {
    private final ConfigUtils configs;
	private static final TypeToken<String> STRING_TOKEN = TypeToken.of(String.class);

    public VelocityModuleManager(ConfigUtils configs) {
        this.configs = configs;
    }

    @Override
    public void reloadData() {
        final ConfigurationNode messagesYml = configs.createAndGet("messages.yml");
		final Map<String, Map<String, String>> locales = new HashMap<>();

		messagesYml.getNode("langs").getChildrenMap().forEach((lang, node) -> {
			final Map<String, String> messages = new HashMap<>();

			node.getChildrenMap().forEach((key, n) -> {
				final String value = n.getString();

				messages.put(key.toString(), value);
			});

			locales.put(lang.toString(), messages);
		});

        final ConfigurationNode blacklistYml = configs.createAndGet("blacklist.yml");
		final ConfigurationNode configYml = configs.createAndGet("config.yml");
		final ConfigurationNode whitelistYml = configs.createAndGet("whitelist.yml");

        ConfigurationNode actual;

        actual = configYml.getNode("caps");

		try {
			getCapsModule().loadData(actual.getNode("enabled").getBoolean(), actual.getNode("replace").getBoolean(),
				actual.getNode("max").getInt(), actual.getNode("warn", "max").getInt(),
				actual.getNode("warn", "notification").getString(),
				actual.getNode("punishments").getList(STRING_TOKEN).toArray(ArraysUtil.EMPTY_ARRAY));
			actual = configYml.getNode("cooldown");
			getCooldownModule().loadData(actual.getNode("enabled").getBoolean(),
					actual.getNode("time", "repeat-global").getInt(), actual.getNode("time", "repeat").getInt(),
					actual.getNode("time", "normal").getInt(), actual.getNode("time", "command").getInt());
			actual = configYml.getNode("flood");
			getFloodModule().loadData(actual.getNode("enabled").getBoolean(), actual.getNode("replace").getBoolean(),
					actual.getNode("warn", "max").getInt(), actual.getNode("pattern").getString(),
					actual.getNode("warn", "notification").getString(),
					actual.getNode("punishments").getList(STRING_TOKEN).toArray(ArraysUtil.EMPTY_ARRAY));
			getMessagesModule().loadData(messagesYml.getNode("default").getString(), locales);
			actual = configYml.getNode("general");
			getGeneralModule().loadData(actual.getNode("sanitize").getBoolean(true),
					actual.getNode("sanitize-names").getBoolean(true),
					actual.getNode("commands").getList(STRING_TOKEN));
			getWhitelistModule().loadData(configYml.getNode("whitelist", "enabled").getBoolean(),
					whitelistYml.getNode("expressions").getList(STRING_TOKEN).toArray(ArraysUtil.EMPTY_ARRAY));
			actual = configYml.getNode("blacklist");
			getBlacklistModule().loadData(actual.getNode("enabled").getBoolean(),
					actual.getNode("fake_message").getBoolean(), actual.getNode("hide_words").getBoolean(),
					actual.getNode("warn", "max").getInt(), actual.getNode("warn", "notification").getString(),
					actual.getNode("punishments").getList(STRING_TOKEN).toArray(ArraysUtil.EMPTY_ARRAY),
					blacklistYml.getNode("expressions").getList(STRING_TOKEN).toArray(ArraysUtil.EMPTY_ARRAY));
			actual = configYml.getNode("syntax");
			getSyntaxModule().loadData(actual.getNode("enabled").getBoolean(), actual.getNode("warn", "max").getInt(),
					actual.getNode("warn", "notification").getString(),
					actual.getNode("whitelist").getList(STRING_TOKEN).toArray(ArraysUtil.EMPTY_ARRAY),
					actual.getNode("punisments").getList(STRING_TOKEN).toArray(ArraysUtil.EMPTY_ARRAY));
		} catch(ObjectMappingException e) {
			e.printStackTrace();
		}
        
    }
    
}
