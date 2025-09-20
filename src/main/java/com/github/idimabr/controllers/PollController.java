package com.github.idimabr.controllers;

import com.github.idimabr.models.Poll;
import com.github.idimabr.models.Vote;
import com.github.idimabr.storage.dao.PollRepository;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PollController {

    private final PollRepository repository;
    private final Map<Long, Poll> pollCache = new ConcurrentHashMap<>();
    private final Map<Long, Map<UUID, Vote>> voteCache = new ConcurrentHashMap<>();

    public PollController(PollRepository repository) {
        this.repository = repository;
        loadAllPolls();
    }

    private void loadAllPolls() {
        try {
            Set<Poll> polls = repository.getPolls();
            if (polls != null && !polls.isEmpty()) {
                for (Poll poll : polls) {
                    if (poll != null) {
                        pollCache.put(poll.getId(), poll);
                    }
                }
                System.out.println("Loaded " + polls.size() + " polls from database");
            } else {
                System.out.println("No polls found in database or database is empty");
            }
        } catch (Exception e) {
            System.err.println("Error loading polls: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Poll> listAllPolls(){
        return new ArrayList<>(pollCache.values());
    }

    public List<Poll> listActivePolls() {
        Instant now = Instant.now();
        return pollCache.values().stream()
                .filter(p -> p.isActive() && p.getExpiresAt().isAfter(now))
                .toList();
    }

    public List<Poll> listExpiredPolls() {
        Instant now = Instant.now();
        return pollCache.values().stream()
                .filter(p -> !p.isActive() || p.getExpiresAt().isBefore(now))
                .toList();
    }

    public void createPoll(String question, List<String> options, long durationSeconds) {
        Instant expiresAt = Instant.now().plusSeconds(durationSeconds);
        final Poll poll = new Poll(
                0,
                question,
                options,
                Instant.now(),
                expiresAt,
                true
        );
        final long id = repository.savePoll(poll);
        poll.setId(id);
        pollCache.put(id, poll);
    }

    public Poll getPoll(long pollId) {
        return pollCache.get(pollId);
    }


    public boolean vote(long pollId, UUID playerId, int optionIndex) {
        if (!pollCache.containsKey(pollId)) return false;

        voteCache.putIfAbsent(pollId, new ConcurrentHashMap<>());
        Map<UUID, Vote> votes = voteCache.get(pollId);
        if (votes.containsKey(playerId)) return false;

        Vote vote = new Vote(
                0,
                pollId,
                playerId,
                optionIndex,
                Instant.now()
        );
        final long id = repository.saveVote(pollId, playerId, optionIndex);
        vote.setId(id);
        votes.put(playerId, vote);
        return true;
    }

    public boolean hasVoted(long pollId, UUID playerId) {
        return voteCache.getOrDefault(pollId, Collections.emptyMap()).containsKey(playerId);
    }

    public Vote getVotePlayer(long pollId, UUID playerId) {
        return voteCache.getOrDefault(pollId, Collections.emptyMap()).get(playerId);
    }

    public boolean isPollActive(long pollId) {
        Poll poll = pollCache.get(pollId);
        return poll != null && poll.isActive() && !isPollExpired(pollId);
    }

    public boolean isPollExpired(long pollId) {
        Poll poll = pollCache.get(pollId);
        if (poll == null) return false;
        Instant now = Instant.now();
        Instant expires = poll.getExpiresAt();
        return now.isAfter(expires);
    }

    public boolean hasPoll(long pollId) {
        return pollCache.containsKey(pollId);
    }

    public void closePoll(long pollId) {
        Poll poll = pollCache.get(pollId);
        if (poll == null) return;

        poll.setActive(false);
        repository.savePoll(poll);
    }

    public void deletePoll(long pollId) {
        repository.deletePoll(pollId);
        pollCache.remove(pollId);
        voteCache.remove(pollId);
    }


    public List<Vote> getVotes(long pollId) {
        return new ArrayList<>(voteCache.getOrDefault(pollId, Collections.emptyMap()).values());
    }

    public int getVotesForOption(long pollId, int optionIndex) {
        return (int) getVotes(pollId).stream()
                .filter(vote -> vote.getOptionIndex() == optionIndex)
                .count();
    }
}
