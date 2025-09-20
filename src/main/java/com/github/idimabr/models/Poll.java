package com.github.idimabr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class Poll {

    private long id;
    private String question;
    private List<String> options;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean active;
}
