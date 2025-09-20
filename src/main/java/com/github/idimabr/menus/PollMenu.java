package com.github.idimabr.menus;

import com.github.idimabr.TrialPoll;
import com.github.idimabr.controllers.PollController;
import com.github.idimabr.models.Poll;
import com.github.idimabr.models.Vote;
import com.github.idimabr.parser.ItemParser;
import com.github.idimabr.util.ConfigUtil;
import com.github.idimabr.util.MessageUtil;
import com.henryfabio.minecraft.inventoryapi.controller.InventoryController;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PollMenu extends PagedInventory {

    private final PollController controller;
    final ConfigUtil messages;
    final ConfigUtil config;

    public PollMenu(TrialPoll plugin, int size, String title) {
        super("inventory.poll", title, size);
        this.messages = plugin.getMessages();
        this.config = plugin.getConfig();
        this.controller = plugin.getController();
        configuration(configuration -> {
            configuration.secondUpdate(1);
        });
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        ViewerConfigurationImpl.Paged configuration = viewer.getConfiguration();

        final int previousSlot = config.getInt("navigation.previous.slot", 0);
        final ItemStack previous = ItemParser.parse(config.getConfigurationSection("navigation.previous"), null);
        configuration.previousPageItem(InventoryItem.of(previous));
        configuration.previousPageSlot(previousSlot);

        final int nextSlot = config.getInt("navigation.next.slot", 8);
        final ItemStack next = ItemParser.parse(config.getConfigurationSection("navigation.next"), null);
        configuration.nextPageItem(InventoryItem.of(next));
        configuration.nextPageSlot(nextSlot);

        final int emptySlot = config.getInt("navigation.empty.slot", 22);
        final ItemStack empty = ItemParser.parse(config.getConfigurationSection("navigation.empty"), null);
        configuration.emptyPageItem(InventoryItem.of(empty));
        configuration.emptyPageSlot(emptySlot);
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(@NotNull PagedViewer pagedViewer) {
        final Player player = pagedViewer.getPlayer();
        List<InventoryItemSupplier> itemSuppliers = new LinkedList<>();
        if (player == null) return itemSuppliers;

        final ConfigurationSection section = config.getConfigurationSection("menus.poll.item-format");
        for (Poll poll : controller.listActivePolls()) {
            final Map<String, String> placeholders = new HashMap<>();
            placeholders.put("question",  MessageUtil.wrap(poll.getQuestion(), 8));
            placeholders.put("options", String.join(", ", poll.getOptions()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneId.systemDefault());
            placeholders.put("expires", formatter.format(poll.getExpiresAt()));
            placeholders.put("id", String.valueOf(poll.getId()));

            List<Vote> votes = controller.getVotes(poll.getId());
            Map<Integer, Integer> voteCount = new HashMap<>();
            for (Vote vote : votes) {
                voteCount.put(vote.getOptionIndex(), voteCount.getOrDefault(vote.getOptionIndex(), 0) + 1);
            }
            placeholders.put("total_votes", String.valueOf(votes.size()));

            itemSuppliers.add(() -> {
                ItemStack itemStack = ItemParser.parse(section, placeholders);
                return InventoryItem.of(itemStack).defaultCallback(e -> {
                    e.setCancelled(true);
                    if(controller.hasVoted(poll.getId(), player.getUniqueId())){
                        final Vote vote = controller.getVotePlayer(poll.getId(), player.getUniqueId());
                        player.sendMessage(MessageUtil.apply(messages.getString("already-vote").replace("{option}", poll.getOptions().get(vote.getOptionIndex()))));
                        return;
                    }

                    final InventoryController inventoryController = InventoryManager.getInventoryController();
                    inventoryController.findInventory("inventory.option").ifPresent(inv -> {
                        inv.openInventory(player, viewer -> {
                            ViewerPropertyMap propertyMap = viewer.getPropertyMap();
                            propertyMap.set("poll_id", String.valueOf(poll.getId()));
                        });
                    });
                });
            });
        }

        return itemSuppliers;
    }
}
