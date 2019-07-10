package com.symphony.platformsolutions.tradebuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {
    private String symbol;
    private String companyName;
    private double latestPrice;
    private double change;
}
