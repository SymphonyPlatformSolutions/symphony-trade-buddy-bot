package com.symphony.platformsolutions.tradebuddy.web;

import authentication.SymExtensionAppRSAAuth;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.platformsolutions.tradebuddy.bot.TradeBuddyController;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import static com.symphony.platformsolutions.tradebuddy.TradeBuddyBot.getBotClient;
import static com.symphony.platformsolutions.tradebuddy.TradeBuddyBot.getConfig;

@Slf4j
@RestController
public class WebController {
    private static final ObjectMapper mapper = new ObjectMapper();
    private TradeBuddyController controller;
    private SymExtensionAppRSAAuth appAuth = null;

    public WebController(TradeBuddyController controller) {
        this.controller = controller;
    }

    @GetMapping("/")
    public String home() {
        return "For development purposes, inject the app bundle via https://[your-pod].symphony.com/client/index.html?bundle=https://localhost:4000/bundle.json";
    }

    @GetMapping("/healthz")
    public String getHealth() {
        return "OK";
    }

    @GetMapping("/appToken")
    public String getAppToken() {
        if (appAuth == null) {
            appAuth = new SymExtensionAppRSAAuth(getConfig());
        }
        return appAuth.appAuthenticate().getAppToken();
    }

    @PostMapping("addToRoom")
    public String addToRoom(
        @RequestHeader("token") String token,
        @RequestBody String body
    ) {
        UserInfo userInfo = appAuth.verifyJWT(token);
        if (userInfo == null) {
            return "Failure";
        }

        try {
            JsonNode request = mapper.readTree(body);
            String symbol = request.path("symbol").asText();
            String companyName = request.path("companyName").asText();
            String roomName = String.format("Trade: $%s %s", symbol, companyName);
            if (roomName.length() > 50)
                roomName = roomName.substring(0, 50);

            RoomSearchQuery query = new RoomSearchQuery();
            query.setQuery(roomName);
            query.setOwner(new NumericId(getBotClient().getBotUserInfo().getId()));
            List<RoomInfo> rooms = getBotClient().getStreamsClient()
                .searchRooms(query, 0, 1).getRooms();

            boolean roomDoesNotExist = rooms.isEmpty() ||
                !rooms.get(0).getRoomAttributes().getName().equalsIgnoreCase(roomName);

            String streamId;
            if (roomDoesNotExist) {
                Room room = new Room();
                room.setName(roomName);
                room.setDescription(String.format("Trade discussions about %s %s", symbol, companyName));
                room.setPublic(true);
                streamId = getBotClient().getStreamsClient().createRoom(room).getRoomSystemInfo().getId();
                log.info("Room created for {} with stream id {}", roomName, streamId);
            } else {
                streamId = rooms.get(0).getRoomSystemInfo().getId();
                log.info("Room found for {} with stream id {}", roomName, streamId);
            }

            long userId = userInfo.getId();

            List<Long> roomMembers = getBotClient().getStreamsClient()
                .getRoomMembers(streamId).stream()
                .map(RoomMember::getId)
                .collect(Collectors.toList());

            if (!roomMembers.contains(userId)) {
                getBotClient().getStreamsClient().addMemberToRoom(streamId, userId);
                controller.sendMessage(streamId, String.format("Welcome <mention uid=\"%d\" />!", userId));
            } else {
                controller.sendMessage(streamId, String.format("Welcome back, <mention uid=\"%d\" />!", userId));
            }
        } catch (IOException e) {
            log.error("Json processing error", e);
            return "Failure";
        }
        return "Success";
    }
}
