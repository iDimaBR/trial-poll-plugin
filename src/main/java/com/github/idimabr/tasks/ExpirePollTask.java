package com.github.idimabr.tasks;

import com.github.idimabr.TrialPoll;
import com.github.idimabr.controllers.PollController;
import com.github.idimabr.models.Poll;
import com.github.idimabr.models.Vote;
import com.github.idimabr.util.ConfigUtil;
import com.github.idimabr.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.util.*;

@AllArgsConstructor
public class ExpirePollTask extends BukkitRunnable {

    private final TrialPoll plugin;
    private final PollController pollController;

    @Override
    public void run() {
        List<Poll> activePolls = pollController.listAllPolls();
        for (Poll poll : activePolls) {
            if(!poll.isActive()) continue;
            if (Instant.now().isAfter(poll.getExpiresAt())) {
                pollController.closePoll(poll.getId());

                List<Vote> votes = pollController.getVotes(poll.getId());
                Map<Integer, Integer> voteCount = new HashMap<>();
                for (Vote vote : votes) {
                    voteCount.put(vote.getOptionIndex(),
                            voteCount.getOrDefault(vote.getOptionIndex(), 0) + 1);
                }

                Optional<Map.Entry<Integer, Integer>> winner = voteCount.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue());

                String winnerText = "None";
                int winnerVotes = 0;
                if (winner.isPresent()) {
                    int optionIndex = winner.get().getKey();
                    winnerVotes = winner.get().getValue();
                    winnerText = poll.getOptions().get(optionIndex);
                }

                ConfigUtil messages = plugin.getMessages();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (String line : messages.getStringList("finished-poll")) {
                        player.sendMessage(MessageUtil.apply(
                                line.replace("{question}", poll.getQuestion())
                                        .replace("{winner}", winnerText)
                                        .replace("{votes}", String.valueOf(winnerVotes)
                                        ).replace("{total_votes}", String.valueOf(votes.size()))
                                        ));
                    }
                }
            }
        }
    }
}
