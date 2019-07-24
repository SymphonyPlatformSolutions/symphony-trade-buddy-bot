package com.symphony.platformsolutions.tradebuddy.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.platformsolutions.tradebuddy.TradeBuddyBot;
import com.symphony.platformsolutions.tradebuddy.model.*;
import com.symphony.platformsolutions.tradebuddy.util.IEXClient;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import lombok.extern.slf4j.Slf4j;
import model.InboundMessage;
import model.OutboundMessage;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static com.symphony.platformsolutions.tradebuddy.util.Constants.*;

@Slf4j
@Service
public class TradeBuddyController {
    private IEXClient iexClient;
    private static List<WatchlistItem> watchlist;
    private static final String IEX_URL = "https://cloud.iexapis.com/v1";
    private static final String IEX_TOKEN = "<iex_api_key>";

    public TradeBuddyController() {
        this.iexClient = Feign.builder()
            .requestInterceptor(requestTemplate -> requestTemplate.query("token", IEX_TOKEN))
            .decoder(new JacksonDecoder())
            .target(IEXClient.class, IEX_URL);
        watchlist = new ArrayList<>();
    }

    public void handleIncoming(InboundMessage inMsg) {
        String messageText = inMsg.getMessageText().trim();
        if (!messageText.startsWith("/")) {
            return;
        }
        String streamId = inMsg.getStream().getStreamId();
        long userId = inMsg.getUser().getUserId();
        String userDisplayName = inMsg.getUser().getDisplayName();

        String command = messageText.substring(1);
        if (command.contains(" ")) {
            command = command.substring(0, command.indexOf(' '));
        }
        List<String> cashtags = inMsg.getCashtags();

        if (inMsg.getStream().getStreamType().equals("IM")) {
            switch (command) {
                case "help":
                    sendMessage(streamId, HELP_TEXT_IM);
                    break;
                case "watchlist":
                    showWatchlist(streamId, userId, userDisplayName);
                    break;
                case "watch":
                    if (isCashtagsInvalid(streamId, userId, cashtags))
                        break;
                    addToWatchlist(streamId, userId, cashtags.get(0));
                    break;
                case "unwatch":
                    if (isCashtagsInvalid(streamId, userId, cashtags))
                        break;
                    removeFromWatchlist(streamId, userId, cashtags.get(0));
                    break;
            }
        } else {
            switch (command) {
                case "help":
                    sendMessage(streamId, HELP_TEXT_ROOM);
                    break;
                case "news":
                    showNews(streamId);
                    break;
                case "price":
                    showPrice(streamId);
                    break;
            }
        }
    }

    private void showWatchlist(String streamId, long userId, String userDisplayName) {
        List<WatchlistItem> filteredWatchlist = watchlist.stream()
            .filter(i -> i.getUserId() == userId)
            .collect(Collectors.toList());
        if (filteredWatchlist.isEmpty()) {
            sendMessage(streamId, String.format(EMPTY_WATCHLIST, userId));
            log.info(EMPTY_WATCHLIST_LOG, userId);
            return;
        }

        List<Quote> quotes = filteredWatchlist.parallelStream()
            .map(i -> iexClient.getQuote(i.getTicker()))
            .collect(Collectors.toList());
        ObjectMapper mapper = new ObjectMapper();

        try {
            WatchlistDataDTO dataDto = new WatchlistDataDTO();
            dataDto.setUserId(userId);
            dataDto.setUserDisplayName(userDisplayName);
            dataDto.setWatchlist(quotes);
            String dtoString = mapper.writeValueAsString(new WatchlistWrapperDTO(dataDto));
            String response = String.format(WATCHLIST_TEXT, userId);

            sendMessage(streamId, response, dtoString);
        } catch (JsonProcessingException e) {
            log.error("JSON Encoding Error", e);
        }
    }

    private boolean isCashtagsInvalid(String streamId, long userId, List<String> cashtags) {
        if (cashtags.size() != 1) {
            sendMessage(streamId, String.format(SPECIFY_ONE_CASHTAG, userId));
            return true;
        }
        return false;
    }

    private void addToWatchlist(String streamId, long userId, String symbol) {
        try {
            WatchlistItem item = new WatchlistItem(userId, symbol);
            if (watchlist.contains(item)) {
                sendMessage(streamId, String.format(WATCHLIST_EXISTS, userId, symbol));
                log.info(WATCHLIST_EXISTS_LOG, symbol, userId);
                return;
            }
            Quote quote = iexClient.getQuote(symbol);
            watchlist.add(item);
            sendMessage(streamId, String.format(WATCHLIST_ADDED, userId, quote.getCompanyName(), symbol));
            log.info(WATCHLIST_ADDED_LOG, quote.getCompanyName(), symbol, userId);
        } catch (Exception e) {
            sendMessage(streamId, String.format(INVALID_SYMBOL, userId, symbol));
            log.info(INVALID_SYMBOL_LOG, symbol, userId);
        }
    }

    private void removeFromWatchlist(String streamId, long userId, String symbol) {
        WatchlistItem item = new WatchlistItem(userId, symbol);
        if (!watchlist.contains(item)) {
            sendMessage(streamId, String.format(SYMBOL_NOT_IN_WATCHLIST, userId, symbol));
            log.info(SYMBOL_NOT_IN_WATCHLIST_LOG, symbol, userId);
            return;
        }
        watchlist.remove(item);
        sendMessage(streamId, String.format(WATCHLIST_REMOVED, userId, symbol));
        log.info(WATCHLIST_REMOVED_LOG, symbol, userId);
    }

    private void showNews(String streamId) {
        String symbol = getSymbolFromRoomName(streamId);
        if (symbol == null) {
            sendMessage(streamId, "Cannot find symbol to search for news on");
            return;
        }

        String newsML = iexClient.getNews(symbol).stream()
            .map(n -> String.format("<li><a href=\"%s\">%s</a></li>", n.getUrl(), n.getHeadline()))
            .collect(Collectors.joining(""));
        String responseML = String.format(NEWS_TEXT, symbol, newsML);
        sendMessage(streamId, responseML);
    }

    private void showPrice(String streamId) {
        String symbol = getSymbolFromRoomName(streamId);
        if (symbol == null) {
            sendMessage(streamId, "Cannot find symbol to search for price on");
            return;
        }

        Quote quote = iexClient.getQuote(symbol);
        double price = quote.getLatestPrice();
        double change = quote.getChange();

        String emoji = (change > 0) ? "chart_with_upwards_trend" :
            (change < 0) ? "chart_with_downwards_trend" : "left_right_arrow";
        String changeText = String.format("<emoji shortcode=\"%s\" />", emoji);
        if (!emoji.equals("left_right_arrow"))
            changeText += String.format(" %.3f", change);

        String responseML = String.format(PRICE_TEXT, symbol, price, changeText);
        sendMessage(streamId, responseML);
    }

    private String getSymbolFromRoomName(String streamId) {
        String roomName = TradeBuddyBot.getBotClient().getStreamsClient().getStreamInfo(streamId)
            .getRoomAttributes().getName();
        Matcher matcher = Pattern.compile("Trade: \\$([\\w]+)").matcher(roomName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public void sendMessage(String streamId, String message, String data) {
        message = message.replaceAll("&", "&amp;");
        TradeBuddyBot.getBotClient().getMessagesClient().sendMessage(streamId, new OutboundMessage(message, data));
    }

    public void sendMessage(String streamId, String message) {
        sendMessage(streamId, message, null);
    }
}
