package com.github.idimabr.storage.adapters;

import com.github.idimabr.models.Poll;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class PollAdapter implements SQLResultAdapter<Poll> {

    @Override
    public Poll adaptResult(SimpleResultSet rs) {
        int id = ((Number) rs.get("id")).intValue();
        String question = rs.get("question");
        String optionsRaw = rs.get("options_json");
        List<String> options = List.of(optionsRaw.split(","));
        Instant createdAt = Instant.ofEpochMilli(((Number) rs.get("created_at")).longValue());
        Instant expiresAt = Instant.ofEpochMilli(((Number) rs.get("expires_at")).longValue());
        boolean active = ((Number) rs.get("active")).intValue() == 1;

        return new Poll(id, question, options, createdAt, expiresAt, active);
    }
}
