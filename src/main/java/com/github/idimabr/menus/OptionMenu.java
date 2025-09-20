package com.github.idimabr.menus;

import com.github.idimabr.TrialPoll;
import com.github.idimabr.controllers.PollController;
import com.github.idimabr.models.Poll;
import com.github.idimabr.models.Vote;
import com.github.idimabr.parser.ItemParser;
import com.github.idimabr.util.ConfigUtil;
import com.github.idimabr.util.MessageUtil;
import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionMenu extends SimpleInventory {

    private final PollController controller;
    final ConfigUtil messages;
    final ConfigUtil config;

    public OptionMenu(TrialPoll plugin, int size, String title) {
        super("inventory.option", title, size);
        this.messages = plugin.getMessages();
        this.config = plugin.getConfig();
        this.controller = plugin.getController();
        configuration(configuration -> {
            configuration.secondUpdate(1);
        });
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        final int pollId = Integer.parseInt(viewer.getPropertyMap().get("poll_id"));
        Player player = viewer.getPlayer();

        final Poll poll = controller.getPoll(pollId);
        List<Vote> votes = controller.getVotes(poll.getId());
        final Map<String, String> placeholders = new HashMap<>();
        placeholders.put("question", MessageUtil.wrap(poll.getQuestion(), 5));
        placeholders.put("options", String.join(", ", poll.getOptions()));
        placeholders.put("id", String.valueOf(poll.getId()));
        placeholders.put("total_votes", String.valueOf(votes.size()));

        final ConfigurationSection questionSection = config.getConfigurationSection("menus.options.items.question");
        int questionSlot = questionSection.getInt("slot");
        editor.setItem(questionSlot,  InventoryItem.of(ItemParser.parse(questionSection, placeholders)).defaultCallback(e -> e.setCancelled(true)));

        int[] slots = config.getIntegerList("menus.options.slots").stream().mapToInt(i -> i).toArray();
        int optionIndex = 0;
        for (String option : poll.getOptions()) {
            if (optionIndex >= slots.length) break;
            int finalOptionIndex = optionIndex;

            final int totalVotes = controller.getVotesForOption(pollId, optionIndex);
            final ConfigurationSection section = config.getConfigurationSection("menus.options.items.option");
            placeholders.put("votes", String.valueOf(totalVotes));
            placeholders.put("option", option);

            editor.setItem(slots[optionIndex], InventoryItem.of(ItemParser.parse(section, placeholders)).defaultCallback(e -> {
                e.setCancelled(true);
                if(!poll.isActive()){
                    player.closeInventory();
                    player.sendMessage(MessageUtil.apply(messages.getString("already-expired")));
                    return;
                }
                if(controller.hasVoted(pollId, player.getUniqueId())){
                    player.sendMessage(MessageUtil.apply(messages.getString("already-vote")));
                    return;
                }

                controller.vote(pollId, player.getUniqueId(), finalOptionIndex);
                player.sendMessage(MessageUtil.apply(messages.getString("vote-recorded").replace("{option}", option)).replace("{votes}", String.valueOf(totalVotes + 1)));
                player.closeInventory();
            }));
            optionIndex++;
        }
    }
}
