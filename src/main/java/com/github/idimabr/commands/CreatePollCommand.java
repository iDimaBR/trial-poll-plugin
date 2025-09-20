package com.github.idimabr.commands;

import com.github.idimabr.TrialPoll;
import com.github.idimabr.controllers.PollController;
import com.github.idimabr.prompts.OptionAddPrompt;
import com.github.idimabr.util.ConfigUtil;
import com.github.idimabr.util.MessageUtil;
import com.github.idimabr.util.TimeParser;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class CreatePollCommand implements CommandExecutor, TabCompleter {

    private final TrialPoll plugin;
    private final PollController controller;

    private static final List<String> TIME_PRESETS = Arrays.asList(
            "30s", "1m", "5m", "10m", "30m", "1h", "2h", "6h", "12h", "1d", "2d", "3d", "1w","1mo"
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String s,
                             @NotNull String[] args) {

        final ConfigUtil messages = plugin.getMessages();
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.apply(messages.getString("only-players")));
            return true;
        }

        if(!sender.hasPermission("trialpoll.create")) {
            sender.sendMessage(MessageUtil.apply(messages.getString("no-permission")));
            return true;
        }

        if (args.length < 2) {
            for (String string : messages.getStringList("help")) {
                sender.sendMessage(
                        MessageUtil.apply(string)
                );
            }
            return true;
        }

        final int seconds = TimeParser.parseTime(args[0]);
        if (seconds <= 0) {
            sender.sendMessage(MessageUtil.apply(messages.getString("invalid-duration")));
            return true;
        }

        final String question = String.join(" ", args).substring(args[0].length()).trim();
        if (question.isEmpty()) {
            sender.sendMessage(MessageUtil.apply(messages.getString("invalid-question")));
            return true;
        }

        final Player player = (Player) sender;
        final ConversationFactory factory = new ConversationFactory(plugin)
                .withLocalEcho(false)
                .withFirstPrompt(new OptionAddPrompt(plugin))
                .addConversationAbandonedListener(event -> {
                    ConversationContext ctx = event.getContext();
                    List<String> options = (List<String>) ctx.getSessionData("options");
                    if (options == null || options.size() < 2) {
                        player.sendMessage(MessageUtil.apply(messages.getString("poll-cancelled")));
                        return;
                    }

                    controller.createPoll(question, options, seconds);
                    player.sendMessage(MessageUtil.apply(messages.getString("poll-created")));
                });

        final Conversation conversation = factory.buildConversation(player);
        conversation.begin();
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (!sender.hasPermission("trialpoll.create")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return TIME_PRESETS.stream()
                    .filter(opt -> opt.startsWith(prefix))
                    .limit(10)
                    .toList();
        }

        return Collections.emptyList();
    }
}