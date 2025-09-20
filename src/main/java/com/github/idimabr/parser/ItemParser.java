package com.github.idimabr.parser;

import com.github.idimabr.util.ItemBuilder;
import com.github.idimabr.util.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemParser {

    public static ItemStack parse(ConfigurationSection section, Map<String, String> placeholders) {
        if (section == null) return null;

        String materialName = section.getString("material");
        if (materialName == null || materialName.isEmpty()) {
            return null;
        }

        final ItemBuilder builder = new ItemBuilder(materialName);
        if (section.isSet("amount")) {
            builder.setAmount(section.getInt("amount", 1));
        }

        if (section.isSet("data")) {
            builder.setDurability((short) section.getInt("data", 0));
        }

        if (section.isSet("name")) {
            String name = applyPlaceholders(section.getString("name", ""), placeholders);
            builder.setName(MessageUtil.apply(name));
        }

        if (section.isSet("lore")) {
            List<String> lore = section.getStringList("lore").stream()
                    .flatMap(line -> applyPlaceholdersLore(line, placeholders, 8).stream())
                    .collect(Collectors.toList());
            builder.setLore(lore);
        }

        if (section.isSet("enchantments")) {
            ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
            if (enchantmentsSection != null) {
                for (String key : enchantmentsSection.getKeys(false)) {
                    String[] parts = key.split(":");
                    if (parts.length == 2) {
                        final Enchantment enchantment = Enchantment.getByName(parts[0]);
                        if (enchantment == null) continue;
                        int level = enchantmentsSection.getInt(key, 1);
                        builder.addUnsafeEnchantment(enchantment, level);
                    }
                }
            }
        }
        return builder.build();
    }

    private static String applyPlaceholders(String text, Map<String, String> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) return text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    private static List<String> applyPlaceholdersLore(String text, Map<String, String> placeholders, int wordsPerLine) {
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                text = text.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        List<String> lines = new ArrayList<>();
        String lastColor = "§f";

        for (String word : words) {
            int index = word.lastIndexOf('§');
            if (index != -1 && index < word.length() - 1) {
                lastColor = word.substring(index, index + 2);
            }

            if (count > 0) sb.append(" ");
            sb.append(word);
            count++;

            if (count >= wordsPerLine) {
                lines.add(MessageUtil.apply(lastColor + sb));
                sb = new StringBuilder();
                count = 0;
            }
        }

        if (!sb.isEmpty()) {
            lines.add(MessageUtil.apply(lastColor + sb));
        }

        return lines;
    }
}
