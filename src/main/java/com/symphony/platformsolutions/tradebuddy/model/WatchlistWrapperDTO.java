package com.symphony.platformsolutions.tradebuddy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WatchlistWrapperDTO {
    @JsonProperty("trade-watchlist")
    private WatchlistDataDTO data;
}
