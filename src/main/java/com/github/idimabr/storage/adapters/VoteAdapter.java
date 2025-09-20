package com.github.idimabr.storage.adapters;

import com.github.idimabr.models.Vote;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class VoteAdapter implements SQLResultAdapter<Vote> {

    @Override
    public Vote adaptResult(SimpleResultSet rs) {
        int id = ((Number) rs.get("id")).intValue();
        int pollId = ((Number) rs.get("poll_id")).intValue();
        UUID playerUUID = UUID.fromString(rs.get("player_uuid"));
        int optionIndex = ((Number) rs.get("option_index")).intValue();
        Instant votedAt = Instant.ofEpochMilli(((Number) rs.get("voted_at")).longValue());

        return new Vote(id, pollId, playerUUID, optionIndex, votedAt);
    }
}
