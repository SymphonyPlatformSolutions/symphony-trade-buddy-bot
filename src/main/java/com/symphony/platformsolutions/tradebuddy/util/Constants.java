package com.symphony.platformsolutions.tradebuddy.util;

public class Constants {
    public static final String HELP_TEXT_IM = "Usage:<ul><li><b>/watch <cash tag=\"ticker\" /></b>: add a ticker to your watchlist</li><li><b>/unwatch <cash tag=\"ticker\" /></b>: remove a ticker from your watchlist</li><li><b>/watchlist</b>: display your watchlist</li></ul>";
    public static final String HELP_TEXT_ROOM = "Usage:<ul><li><b>/watch <cash tag=\"ticker\" /></b>: add a ticker to your watchlist</li><li><b>/unwatch <cash tag=\"ticker\" /></b>: remove a ticker from your watchlist</li><li><b>/watchlist</b>: display your watchlist</li><li><b>/price</b>: gets the current price for this room's ticker</li><li><b>/news</b>: gets the latest news for this room's ticker</li></ul>";
    public static final String SPECIFY_ONE_CASHTAG = "Hi <mention uid=\"%d\" />, please specify 1 <cash tag=\"cashtag\" />";
    public static final String WATCHLIST_EXISTS = "Hi <mention uid=\"%d\"/>, <cash tag=\"%s\"/> is already on your watchlist";
    public static final String WATCHLIST_EXISTS_LOG = "Watchlist item {} already exists for {}";
    public static final String WATCHLIST_ADDED = "Hi <mention uid=\"%d\"/>, added %s (<cash tag=\"%s\"/>) to your watchlist";
    public static final String WATCHLIST_ADDED_LOG = "Added new watchlist item {} (${}) for {}";
    public static final String INVALID_SYMBOL = "Hi <mention uid=\"%d\"/>, no security found for ticker <cash tag=\"%s\"/>";
    public static final String INVALID_SYMBOL_LOG = "No security found for ${} for {}";
    public static final String SYMBOL_NOT_IN_WATCHLIST = "Hi <mention uid=\"%d\"/>, the ticker <cash tag=\"%s\"/> is not in your watchlist";
    public static final String SYMBOL_NOT_IN_WATCHLIST_LOG = "Ticker ${} not in watchlist for {}";
    public static final String WATCHLIST_REMOVED = "Hi <mention uid=\"%d\"/>, removed ticker <cash tag=\"%s\"/> from your watchlist";
    public static final String WATCHLIST_REMOVED_LOG = "Removed watchlist item {} for ${}";
    public static final String EMPTY_WATCHLIST = "Hi <mention uid=\"%d\" />, there are no items in your watchlist. Use <b>/watch <cash tag=\"ticker\" /></b> to get started.";
    public static final String EMPTY_WATCHLIST_LOG = "User {} tried to fetch empty watchlist";
    public static final String WATCHLIST_TEXT = "<div class=\"entity\" data-entity-id=\"trade-watchlist\"><b><i>Please install the TradeBuddy App from Symphony Market to display this message.</i></b></div>";
    public static final String NEWS_TEXT = "Here's the latest news on <cash tag=\"%s\" /><ul>%s</ul>";
    public static final String PRICE_TEXT = "Latest price for <cash tag=\"%s\" /> is %.3f, %s today";
}
