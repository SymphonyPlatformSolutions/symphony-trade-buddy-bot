package com.symphony.platformsolutions.tradebuddy.util;

import com.symphony.platformsolutions.tradebuddy.model.News;
import com.symphony.platformsolutions.tradebuddy.model.Quote;
import feign.Param;
import feign.RequestLine;
import java.util.List;

public interface IEXClient {
    @RequestLine("GET /stock/{symbol}/quote")
    Quote getQuote(@Param("symbol") String symbol);

    @RequestLine("GET /stock/{symbol}/news/last")
    List<News> getNews(@Param("symbol") String symbol);
}
