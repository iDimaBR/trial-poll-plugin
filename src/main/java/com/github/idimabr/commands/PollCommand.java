package com.github.idimabr.commands;

import com.github.idimabr.controllers.PollController;
import com.github.idimabr.menus.ResultsMenu;
import com.github.idimabr.util.ConfigUtil;
import com.github.idimabr.util.MessageUtil;
import com.henryfabio.minecraft.inventoryapi.controller.InventoryController;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class PollCommand implements CommandExecutor, TabCompleter {

    private final ConfigUtil messages;
    private final PollController controller;

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String s,
                             @NotNull String[] args) {

        final InventoryController inventoryController = InventoryManager.getInventoryController();
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtil.apply(messages.getString("only-players")));
                return true;
            }

            if(!sender.hasPermission("trialpoll.use")) {
                sender.sendMessage(MessageUtil.apply(messages.getString("no-permission")));
                return true;
            }

            Player player = (Player) sender;
            inventoryController.findInventory("inventory.poll").ifPresent(inv -> {
                inv.openInventory(player, viewer -> {});
            });
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("results")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtil.apply(messages.getString("only-players")));
                return true;
            }

            if(!sender.hasPermission("trialpoll.results")) {
                sender.sendMessage(MessageUtil.apply(messages.getString("no-permission")));
                return true;
            }

            Player player = (Player) sender;
            inventoryController.findInventory("inventory.results").ifPresent(inv -> {
                inv.openInventory(player, viewer -> {});
            });
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("close")) {
            if(!sender.hasPermission("trialpoll.close")) {
                sender.sendMessage(MessageUtil.apply(messages.getString("no-permission")));
                return true;
            }

            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtil.apply(messages.getString("invalid-poll")));
                return true;
            }

            if (!controller.hasPoll(id) || !controller.isPollActive(id)) {
                sender.sendMessage(
                        MessageUtil.apply(
                                messages.getString("not-found-poll")
                                        .replace("{id}", id+"")
                        )
                );
                return true;
            }

            controller.closePoll(id);
            sender.sendMessage(
                    MessageUtil.apply(
                            messages.getString("poll-closed")
                                    .replace("{id}", id+"")
                    )
            );
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            if(!sender.hasPermission("trialpoll.remove")) {
                sender.sendMessage(MessageUtil.apply(messages.getString("no-permission")));
                return true;
            }

            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtil.apply(messages.getString("invalid-poll")));
                return true;
            }

            if (!controller.hasPoll(id)) {
                sender.sendMessage(
                        MessageUtil.apply(
                                messages.getString("not-found-poll")
                                        .replace("{id}", id+"")
                        )
                );
                return true;
            }

            controller.deletePoll(id);
            sender.sendMessage(
                    MessageUtil.apply(
                            messages.getString("poll-removed")
                                    .replace("{id}", id+"")
                    )
            );
            return true;
        }

        for (String string : messages.getStringList("help")) {
            sender.sendMessage(
                    MessageUtil.apply(string)
            );
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> options = new ArrayList<>(3);
            if (sender.hasPermission("trialpoll.results") && prefix.startsWith("results")) {
                options.add("results");
            }
            if (sender.hasPermission("trialpoll.close") && prefix.startsWith("close")) {
                options.add("close");
            }
            if (sender.hasPermission("trialpoll.remove") && prefix.startsWith("remove")) {
                options.add("remove");
            }

            return options.isEmpty() ? Collections.emptyList() : options;
        }

        if (args.length == 2) {
            String prefix = args[1].toLowerCase();
            if (args[0].equalsIgnoreCase("close") && sender.hasPermission("trialpoll.close")) {
                return controller.listActivePolls().stream()
                        .map(p -> String.valueOf(p.getId()))
                        .filter(id -> id.startsWith(prefix))
                        .limit(30)
                        .toList();
            }

            if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("trialpoll.remove")) {
                return controller.listAllPolls().stream()
                        .map(p -> String.valueOf(p.getId()))
                        .filter(id -> id.startsWith(prefix))
                        .limit(30)
                        .toList();
            }
        }

        return Collections.emptyList();
    }
}
