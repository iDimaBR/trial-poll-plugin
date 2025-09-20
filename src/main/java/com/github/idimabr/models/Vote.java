package com.github.idimabr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class Vote {

    private long id;
    private long pollId;
    private UUID playerUuid;
    private int optionIndex;
    private Instant votedAt;

}
