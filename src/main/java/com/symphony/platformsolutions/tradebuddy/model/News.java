package com.symphony.platformsolutions.tradebuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class News {
    private String headline;
    private String url;
}
