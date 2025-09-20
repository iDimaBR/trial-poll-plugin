package com.github.idimabr.menus;


import com.github.idimabr.TrialPoll;
import com.github.idimabr.controllers.PollController;
import com.github.idimabr.models.Poll;
import com.github.idimabr.models.Vote;
import com.github.idimabr.parser.ItemParser;
import com.github.idimabr.util.ConfigUtil;
import com.github.idimabr.util.MessageUtil;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ResultsMenu extends PagedInventory {

    private final PollController controller;
    final ConfigUtil config;

    public ResultsMenu(TrialPoll plugin, int size, String title) {
        super("inventory.results", title, size);
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

        final ConfigurationSection section = config.getConfigurationSection("menus.results.item-format");
        for (Poll poll : controller.listExpiredPolls()) {
            final Map<String, String> placeholders = new HashMap<>();
            placeholders.put("question",  MessageUtil.wrap(poll.getQuestion(), 5));
            placeholders.put("options", String.join(", ", poll.getOptions()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneId.systemDefault());
            placeholders.put("created", formatter.format(poll.getCreatedAt()));
            placeholders.put("id", String.valueOf(poll.getId()));

            List<Vote> votes = controller.getVotes(poll.getId());
            Map<Integer, Integer> voteCount = new HashMap<>();
            for (Vote vote : votes) {
                voteCount.put(vote.getOptionIndex(), voteCount.getOrDefault(vote.getOptionIndex(), 0) + 1);
            }

            Optional<Map.Entry<Integer, Integer>> winner = voteCount.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());

            for (int i = 0; i < 6; i++) {
                String line;
                if (i < poll.getOptions().size()) {
                    String option = poll.getOptions().get(i);
                    int count = voteCount.getOrDefault(i, 0);
                    if (winner.isPresent() && winner.get().getKey() == i) {
                        line = "§a" + option + " - " + count + " votes (winner)";
                    } else {
                        line = "§7" + option + " - " + count + " votes";
                    }
                } else {
                    line = "";
                }
                placeholders.put("option_" + (i + 1), line);
            }

            winner.ifPresent(w -> {
                String option = poll.getOptions().get(w.getKey());
                int count = w.getValue();
                placeholders.put("winner", "§a" + option + " - " + count + " votes");
            });

            itemSuppliers.add(() -> {
                ItemStack itemStack = ItemParser.parse(section, placeholders);
                return InventoryItem.of(itemStack).defaultCallback(e -> {
                    e.setCancelled(true);
                });
            });
        }

        return itemSuppliers;
    }
}
