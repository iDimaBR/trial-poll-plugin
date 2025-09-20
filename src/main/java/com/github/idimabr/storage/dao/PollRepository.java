package com.github.idimabr.storage.dao;

import com.github.idimabr.TrialPoll;
import com.github.idimabr.models.Poll;
import com.github.idimabr.models.Vote;
import com.github.idimabr.storage.Database;
import com.github.idimabr.storage.SQLHelper;
import com.github.idimabr.storage.adapters.PollAdapter;
import com.github.idimabr.storage.adapters.VoteAdapter;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PollRepository {

    private final TrialPoll plugin;
    private final SQLConnector connection;

    public PollRepository(TrialPoll plugin, SQLConnector connection) {
        this.plugin = plugin;
        this.connection = connection;
    }

    public void createTables() {
        SQLExecutor executor = executor();

        String pollsTable = createPollsTableSQL();
        String votesTable = createVotesTableSQL();

        executor.updateQuery(pollsTable);
        executor.updateQuery(votesTable);

        plugin.getLogger().info("Tables created for " + Database.databaseType.toUpperCase() + " database");
    }

    private String createPollsTableSQL() {
        if (Database.isSQLITE()) {
            return "CREATE TABLE IF NOT EXISTS polls (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "question VARCHAR(255) NOT NULL, " +
                    "options_json TEXT NOT NULL, " +
                    "created_at BIGINT NOT NULL DEFAULT (strftime('%s','now')), " +
                    "expires_at BIGINT NOT NULL, " +
                    "active BOOLEAN DEFAULT TRUE" +
                    ");";
        } else {
            // MySQL
            return "CREATE TABLE IF NOT EXISTS polls (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "question VARCHAR(255) NOT NULL, " +
                    "options_json TEXT NOT NULL, " +
                    "created_at BIGINT NOT NULL DEFAULT UNIX_TIMESTAMP(), " +
                    "expires_at BIGINT NOT NULL, " +
                    "active BOOLEAN DEFAULT TRUE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
        }
    }

    private String createVotesTableSQL() {
        if (Database.isSQLITE()) {
            return "CREATE TABLE IF NOT EXISTS votes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "poll_id INTEGER NOT NULL, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "option_index INT NOT NULL, " +
                    "voted_at BIGINT NOT NULL DEFAULT (strftime('%s','now')), " +
                    "FOREIGN KEY (poll_id) REFERENCES polls(id) ON DELETE CASCADE," +
                    "UNIQUE (poll_id, player_uuid)" +
                    ");";
        } else {
            // MySQL
            return "CREATE TABLE IF NOT EXISTS votes (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "poll_id BIGINT NOT NULL, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "option_index INT NOT NULL, " +
                    "voted_at BIGINT NOT NULL DEFAULT UNIX_TIMESTAMP(), " +
                    "FOREIGN KEY (poll_id) REFERENCES polls(id) ON DELETE CASCADE," +
                    "UNIQUE KEY unique_vote (poll_id, player_uuid)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
        }
    }

    public long savePoll(Poll poll) {
        SQLHelper helper = new SQLHelper(connection);
        String sql;

        if (Database.isSQLITE()) {
            sql = "INSERT OR REPLACE INTO polls(question, options_json, expires_at, active) " +
                    "VALUES (?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO polls(question, options_json, expires_at, active) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "question = VALUES(question), " +
                    "options_json = VALUES(options_json), " +
                    "expires_at = VALUES(expires_at), " +
                    "active = VALUES(active)";
        }

        return helper.updateAndReturn(sql, stmt -> {
            try {
                stmt.setString(1, poll.getQuestion());
                stmt.setString(2, String.join(",", poll.getOptions()));
                stmt.setLong(3, poll.getExpiresAt().toEpochMilli());
                stmt.setBoolean(4, poll.isActive());
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to set poll parameters: " + e.getMessage());
            }
        });
    }

    public Set<Poll> getPolls() {
        try {
            Set<Poll> result = executor().resultManyQuery(
                    "SELECT * FROM polls ORDER BY created_at DESC",
                    stmt -> {},
                    PollAdapter.class
            );
            Set<Poll> validPolls = new HashSet<>();

            if (result != null) {
                for (Poll poll : result) {
                    if (poll != null && poll.getId() > 0) {
                        validPolls.add(poll);
                    } else {
                        plugin.getLogger().warning("Skipping invalid poll with null or zero ID");
                    }
                }
            }

            plugin.getLogger().info("Retrieved " + validPolls.size() + " valid polls from " + Database.databaseType.toUpperCase());
            return validPolls;
        } catch (Exception e) {
            plugin.getLogger().warning("Error retrieving polls: " + e.getMessage());
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public long saveVote(long pollId, UUID playerUUID, int optionIndex) {
        SQLHelper helper = new SQLHelper(connection);
        String sql = Database.isSQLITE() ?
                "INSERT OR REPLACE INTO votes(poll_id, player_uuid, option_index) VALUES(?, ?, ?)" :
                "REPLACE INTO votes(poll_id, player_uuid, option_index) VALUES(?, ?, ?)";

        long id = helper.updateAndReturn(sql, stmt -> {
            try {
                stmt.setLong(1, pollId);
                stmt.setString(2, playerUUID.toString());
                stmt.setInt(3, optionIndex);
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to set vote parameters: " + e.getMessage());
            }
        });

        if (id > 0) {
            plugin.getLogger().info("Vote saved with ID: " + id + " for poll " + pollId + " (Database: " + Database.databaseType.toUpperCase() + ")");
        } else {
            plugin.getLogger().warning("Failed to save vote - no ID generated");
        }

        return id;
    }

    public void saveVotesBatch(List<Vote> votes) {
        if (votes == null || votes.isEmpty()) {
            plugin.getLogger().info("No votes to save in batch");
            return;
        }

        String sql = Database.isSQLITE() ?
                "INSERT OR REPLACE INTO votes(poll_id, player_uuid, option_index) VALUES(?, ?, ?)" :
                "REPLACE INTO votes(poll_id, player_uuid, option_index) VALUES(?, ?, ?)";

        try {
            new SQLHelper(connection).batchUpdate(sql, ps -> {
                for (Vote vote : votes) {
                    try {
                        ps.setLong(1, vote.getPollId());
                        ps.setString(2, vote.getPlayerUuid().toString());
                        ps.setInt(3, vote.getOptionIndex());
                        ps.addBatch();
                    } catch (SQLException e) {
                        plugin.getLogger().warning("Failed to add vote to batch: " + e.getMessage());
                    }
                }
            });
            plugin.getLogger().info("Batch saved " + votes.size() + " votes (" + Database.databaseType.toUpperCase() + ")");
        } catch (Exception e) {
            plugin.getLogger().warning("Error in batch vote save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Set<Vote> getVotesByPoll(long pollId) {
        try {
            Set<Vote> result = executor().resultManyQuery(
                    "SELECT * FROM votes WHERE poll_id = ? ORDER BY voted_at ASC",
                    stmt -> stmt.set(1, pollId),
                    VoteAdapter.class
            );

            Set<Vote> validVotes = new HashSet<>();
            if (result != null) {
                for (Vote vote : result) {
                    if (vote != null && vote.getId() > 0) {
                        validVotes.add(vote);
                    }
                }
            }

            return validVotes;
        } catch (Exception e) {
            plugin.getLogger().warning("Error retrieving votes for poll " + pollId + ": " + e.getMessage());
            return new HashSet<>();
        }
    }

    public boolean hasVoted(long pollId, UUID playerUUID) {
        try {
            Set<Vote> votes = executor().resultManyQuery(
                    "SELECT id FROM votes WHERE poll_id = ? AND player_uuid = ? LIMIT 1",
                    stmt -> {
                        stmt.set(1, pollId);
                        stmt.set(2, playerUUID.toString());
                    },
                    VoteAdapter.class
            );
            return votes != null && !votes.isEmpty();
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking if player has voted: " + e.getMessage());
            return false;
        }
    }

    public void deletePoll(long pollId) {
        try {
            executor().updateQuery(
                    "DELETE FROM polls WHERE id = ?",
                    stmt -> stmt.set(1, pollId)
            );
            plugin.getLogger().info("Poll deleted: " + pollId + " (" + Database.databaseType.toUpperCase() + ")");
        } catch (Exception e) {
            plugin.getLogger().warning("Error deleting poll " + pollId + ": " + e.getMessage());
        }
    }

    private SQLExecutor executor() {
        return new SQLExecutor(connection);
    }

    public String getDatabaseType() {
        return Database.databaseType;
    }

    public boolean isSQLite() {
        return Database.isSQLITE();
    }
}