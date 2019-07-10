package com.symphony.platformsolutions.tradebuddy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class WatchlistItem {
    private long userId;
    private String ticker;
}
