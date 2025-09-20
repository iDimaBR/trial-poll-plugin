package com.github.idimabr.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_HEX = LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static Component toComponent(String message) {
        if (isEmpty(message)) return Component.empty();
        message = message.replace('&', '§');
        if (message.contains("§")) {
            return LEGACY_HEX.deserialize(message);
        }
        return MINI_MESSAGE.deserialize(message);
    }

    public static String apply(String message) {
        if (isEmpty(message)) return "";
        return LEGACY_HEX.serialize(toComponent(message));
    }

    public static String wrap(String text, int wordsPerLine) {
        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (String word : words) {
            if (count > 0) sb.append(" ");
            sb.append(word);
            count++;

            if (count >= wordsPerLine) {
                sb.append("\n"); // quebra de linha
                count = 0;
            }
        }
        return sb.toString().trim();
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
