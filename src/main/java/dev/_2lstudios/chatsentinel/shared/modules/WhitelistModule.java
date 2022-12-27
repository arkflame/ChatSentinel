package dev._2lstudios.chatsentinel.shared.modules;

import java.util.regex.Pattern;

import dev._2lstudios.chatsentinel.shared.utils.PatternUtil;

public class WhitelistModule {
    private boolean enabled;
    private Pattern pattern;

    public void loadData(boolean enabled, String[] patterns) {
        this.enabled = enabled;
        this.pattern = PatternUtil.compile(patterns);
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
