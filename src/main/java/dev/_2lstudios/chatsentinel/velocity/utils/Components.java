package dev._2lstudios.chatsentinel.velocity.utils;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Components {
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
        .character(LegacyComponentSerializer.AMPERSAND_CHAR)
        .hexColors()
        .build();
}
