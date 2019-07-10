package com.symphony.platformsolutions.tradebuddy.model;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class WatchlistDataDTO {
    private final String type = "com.symphony.ps.watchlist";
    private final String version = "1.0";
    private final String renderId = UUID.randomUUID().toString();
    private List<Quote> watchlist;
    private long userId;
    private String userDisplayName;
}
